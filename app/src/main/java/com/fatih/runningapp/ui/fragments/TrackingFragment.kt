package com.fatih.runningapp.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.fatih.runningapp.R
import com.fatih.runningapp.databinding.FragmentTrackingBinding
import com.fatih.runningapp.entities.Run
import com.fatih.runningapp.services.Polyline
import com.fatih.runningapp.services.TrackingService
import com.fatih.runningapp.util.Constants.ACTION_PAUSE_SERVICE
import com.fatih.runningapp.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.fatih.runningapp.util.Constants.ACTION_STOP_SERVICE
import com.fatih.runningapp.util.TrackingUtility
import com.fatih.runningapp.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) ,MenuProvider {

    private lateinit var binding:FragmentTrackingBinding
    private val viewModel: MainViewModel by viewModels()
    private var map:GoogleMap?=null
    private var isTracking=false
    @set:Inject
    var weight=80f
    private var menu:Menu?=null
    private var pathPoints= mutableListOf<Polyline>()
    private var currentTimeInMillis=0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.RESUMED)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding=DataBindingUtil.bind(view)!!
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map=it
            addAllPolylines()
        }
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
        subscribeToObservers()
        super.onViewCreated(view, savedInstanceState)
    }
    private fun sendCommandToService(action:String){

        Intent(requireContext(),TrackingService::class.java).apply {
            this.action=action
            requireContext().startService(this)
        }
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.get(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun moveCameraToUserPosition(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(),10f))
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions=PolylineOptions().color(Color.RED).width(8f).addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatlng=pathPoints.last()[pathPoints.last().size-2]
            var lastLatlng=pathPoints.last().last()
            val polylineOptions=PolylineOptions().color(Color.RED).width(8f).add(preLastLatlng).add(lastLatlng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner){
            updateTracking(it)
        }
        TrackingService.pathPoints.observe(viewLifecycleOwner){
            this.pathPoints=it
            addLatestPolyline()
            moveCameraToUserPosition()
        }
        TrackingService.timeInMillis.observe(viewLifecycleOwner){
            currentTimeInMillis=it
            val text=TrackingUtility.getFormattedStopwatchTime(currentTimeInMillis,true)
            binding.tvTimer.text=text
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking && currentTimeInMillis>0){
            binding.btnToggleRun.text="Start"
            binding.btnFinishRun.visibility=View.VISIBLE
        }else if(isTracking){
            binding.btnToggleRun.text="Stop"
            menu?.get(0)?.isVisible=true
            binding.btnFinishRun.visibility=View.GONE
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds=LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
            binding.mapView.width,
            binding.mapView.height,
            (binding.mapView.height*0.05f).toInt()))
    }

    private fun endRunAndSaveToDb(){
        map?.snapshot { bmp->
            var distanceInMeter=0
            for(polyline in pathPoints){
                distanceInMeter+=TrackingUtility.calculatePolylineLength(polyline).toInt()

            }
            val avgSpeed = round((distanceInMeter / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp=Calendar.getInstance().timeInMillis
            val caloriesBurned=((distanceInMeter/1000f)*weight).toInt()
            val run= Run(bmp,dateTimeStamp,avgSpeed,distanceInMeter,currentTimeInMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.rootView),
            "Run saved successfully",Snackbar.LENGTH_LONG).show()
            stopRun()
        }
    }


    private fun showCancelTrackingDialog(){
       MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel the run").setMessage("Are you sure to cancel the run")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){ _,_->
                stopRun()
            }
            .setNegativeButton("No"){dialog,_->
                dialog.cancel()
            }
            .create().show()
    }

    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        this.menu=menu

        if (currentTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.cancelTracking->{
                showCancelTrackingDialog()
            }
        }
        return true
    }


    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onLowMemory() {
        binding.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

}