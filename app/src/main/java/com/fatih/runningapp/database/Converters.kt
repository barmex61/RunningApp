package com.fatih.runningapp.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


class Converters {

    @TypeConverter
    fun toBitmap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

    @TypeConverter
    fun fromBitmap(bmp:Bitmap):ByteArray{
        val byteArray=ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,byteArray)
        return byteArray.toByteArray()
    }
}