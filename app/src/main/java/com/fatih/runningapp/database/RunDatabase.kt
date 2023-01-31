package com.fatih.runningapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fatih.runningapp.entities.Run


@Database(entities = [Run::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunDatabase :RoomDatabase(){
    abstract fun runDao():RunDao
}