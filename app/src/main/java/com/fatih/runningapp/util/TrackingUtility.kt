package com.fatih.runningapp.util

import android.Manifest.permission.*
import android.content.Context
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment
import com.fatih.runningapp.services.Polyline
import com.fatih.runningapp.util.Constants.PERMISSION_REQUEST_CODE
import pub.devrel.easypermissions.EasyPermissions
import java.sql.Time
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context)=
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            EasyPermissions.hasPermissions(context,ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        else
            EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                ACCESS_BACKGROUND_LOCATION)


    fun requestPermissions(host:Fragment){
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                host,
                "You need to give permission",
                PERMISSION_REQUEST_CODE,
                ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                host,
                "You need to give permission",
                PERMISSION_REQUEST_CODE,
                ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION)
        }

    }

    fun getFormattedStopwatchTime(ms:Long,includeMillis:Boolean): String {
        var millis=ms
        val hours=TimeUnit.MILLISECONDS.toHours(millis)
        millis-=TimeUnit.HOURS.toMillis(hours)
        val minute=TimeUnit.MILLISECONDS.toMinutes(millis)
        millis-=TimeUnit.MINUTES.toMillis(minute)
        val second=TimeUnit.MILLISECONDS.toSeconds(millis)
        return if(!includeMillis){
            "${if(hours<10) "0" else ""}$hours:"+
                    "${if(minute<10)"0" else ""}$minute:"+
                    "${if(second<10) "0" else ""}$second"
        }else{
            millis-=TimeUnit.SECONDS.toMillis(second)
            millis/=10
            "${if(hours<10) "0" else ""}$hours:"+
                    "${if(minute<10)"0" else ""}$minute:"+
                    "${if(second<10) "0" else ""}$second:"+
                    "${if(millis<10) "0" else ""}$millis"
        }

    }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for(i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}