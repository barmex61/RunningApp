package com.fatih.runningapp.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fatih.runningapp.R
import com.fatih.runningapp.databinding.MarkerViewBinding
import com.fatih.runningapp.entities.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
     c: Context,
    val layoutId: Int,
) : MarkerView(c, layoutId) {


    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null) {
            return
        }

        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDate).text= dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedKmh}km/h"
        findViewById<TextView>(R.id.tvAvgSpeed).text= avgSpeed

        val distanceInKm = "${run.distanceInMeters / 1000f}km"
        findViewById<TextView>(R.id.tvDistanceM).text= distanceInKm

        findViewById<TextView>(R.id.tvDuration).text= TrackingUtility.getFormattedStopwatchTime(run.timeInMillis,false)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        findViewById<TextView>(R.id.tvCaloriesBurned).text= caloriesBurned
    } }

