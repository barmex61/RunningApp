package com.fatih.runningapp.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Run(

    var image: Bitmap?=null,
    var timeStamp:Long=0L,
    var avgSpeedKmh:Float=0f,
    var distanceInMeters:Int=0,
    var timeInMillis:Long=0L,
    var caloriesBurned:Int=0

){

    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}
