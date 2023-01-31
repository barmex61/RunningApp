package com.fatih.runningapp.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fatih.runningapp.R
import com.fatih.runningapp.databinding.ItemRunBinding
import com.fatih.runningapp.entities.Run
import com.fatih.runningapp.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private val diffUtil=object :DiffUtil.ItemCallback<Run>(){
        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id==newItem.id
        }
    }

    private val differ=AsyncListDiffer(this,diffUtil)

    var runList:List<Run>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class RunViewHolder(val binding:ItemRunBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding=DataBindingUtil.inflate<ItemRunBinding>(LayoutInflater.from(parent.context), R.layout.item_run,parent,false)
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = runList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.image).into(holder.binding.ivRunImage)
            val calendar=Calendar.getInstance().apply {
                timeInMillis=run.timeStamp
            }
            val dateFormat=SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            holder.binding.tvDate.text=dateFormat.format(calendar.time)
            val avgSpeed="${run.avgSpeedKmh} Kmh"
            holder.binding.tvAvgSpeed.text=avgSpeed
            val distanceInMeter="${run.distanceInMeters/1000f}km"
            holder.binding.tvDistance.text=distanceInMeter
            holder.binding.tvTime.text=TrackingUtility.getFormattedStopwatchTime(run.timeInMillis,false)
            val caloriesBurned="${run.caloriesBurned} calories"
            holder.binding.tvCalories.text=caloriesBurned
        }
    }
    override fun getItemCount(): Int {
        return runList.size
    }
}