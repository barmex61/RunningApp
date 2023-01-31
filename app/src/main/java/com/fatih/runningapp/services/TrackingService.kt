package com.fatih.runningapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.fatih.runningapp.R
import com.fatih.runningapp.ui.activities.MainActivity
import com.fatih.runningapp.util.Constants.ACTION_PAUSE_SERVICE
import com.fatih.runningapp.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.fatih.runningapp.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.fatih.runningapp.util.Constants.ACTION_STOP_SERVICE
import com.fatih.runningapp.util.Constants.FASTEST_REQUEST_INTERVAL
import com.fatih.runningapp.util.Constants.LOCATION_REQUEST_INTERVAL
import com.fatih.runningapp.util.Constants.NOTIFICATION_CHANNEL_ID
import com.fatih.runningapp.util.Constants.NOTIFICATION_ID
import com.fatih.runningapp.util.Constants.NOTIFICATION_NAME
import com.fatih.runningapp.util.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline=MutableList<LatLng>
typealias Polylines=MutableList<Polyline>

@AndroidEntryPoint
class TrackingService:LifecycleService() {

    @Inject
    lateinit var notificationCompat:NotificationCompat.Builder

    @Inject
    lateinit var currentNotification:NotificationCompat.Builder

    var isFirsRun:Boolean=false
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private var timeRunInSeconds=MutableLiveData<Long>()
    private var isTimerEnabled=false
    private var lapTime=0L
    private var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimestamp=0L
    var serviceKilled=false

    companion object{

        var isTracking=MutableLiveData<Boolean>()
        var pathPoints=MutableLiveData<Polylines>()
        var timeInMillis=MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        currentNotification=notificationCompat
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this){
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeInMillis.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            when(it.action){
                ACTION_START_OR_RESUME_SERVICE->{
                    if(!isFirsRun){
                        Timber.d("start service")
                        isFirsRun=true
                        createForegroundService()
                    }else{
                        Timber.d("Resume service")
                        startTimeRun()
                    }
                }
                ACTION_PAUSE_SERVICE->{
                    pauseService()
                    Timber.d("pause")
                }
                ACTION_STOP_SERVICE->{
                    killService()
                    Timber.d("stop")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText=if(isTracking) "Pause" else "Resume"
        val pendingIntent=if (isTracking){
            val pauseIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_PAUSE_SERVICE
            }
            getService(this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            val resumeIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_START_OR_RESUME_SERVICE
            }
            getService(this,2, resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        currentNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(currentNotification,ArrayList<NotificationCompat.Action>())
        }
        if(!serviceKilled){
            currentNotification=notificationCompat.addAction(R.drawable.ic_pause_black_24dp,notificationActionText,pendingIntent)
            notificationManager.notify(NOTIFICATION_ID,currentNotification.build())
        }

    }

    private fun startTimeRun(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                lapTime=System.currentTimeMillis()-timeStarted
                timeInMillis.postValue(timeRun+lapTime)
                if(timeInMillis.value!!>=lastSecondTimestamp +1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1 )
                    lastSecondTimestamp+=1000L
                }
                delay(50L)
            }
            timeRun+=lapTime
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    private fun addEmptyPolyline(){

        pathPoints.value?.apply {
            add(mutableListOf())
            pathPoints.postValue(this)
        }?: pathPoints.postValue(mutableListOf(mutableListOf()))
    }

    private fun addPathPoint(location:Location?){
        location?.let {
            val pos=LatLng(it.latitude,it.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request=LocationRequest.Builder(PRIORITY_HIGH_ACCURACY,
                    LOCATION_REQUEST_INTERVAL).apply {
                        setMinUpdateIntervalMillis(FASTEST_REQUEST_INTERVAL)
                }.build()
                fusedLocationProviderClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object :LocationCallback (){
        override fun onLocationResult(result: LocationResult) {
            if(isTracking.value!!){
                for(location in result.locations){
                    addPathPoint(location)
                    Timber.d("${location.latitude}")
                }
            }
        }
    }

    private fun createForegroundService(){

        startTimeRun()
        isTracking.postValue(true)

        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME,NotificationManager.IMPORTANCE_LOW).apply {
                this.lightColor= Color.BLUE
                this.enableLights(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)

        }
        startForeground(NOTIFICATION_ID,notificationCompat.build())
        timeRunInSeconds.observe(this){
            if(!serviceKilled){
                val notification=currentNotification.setContentText(TrackingUtility.getFormattedStopwatchTime(it*1000,false))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }
        }

    }

    private fun killService(){
        serviceKilled=true
        isFirsRun=true
        postInitialValues()
        pauseService()
        stopForeground(true)
        stopSelf()
    }
}