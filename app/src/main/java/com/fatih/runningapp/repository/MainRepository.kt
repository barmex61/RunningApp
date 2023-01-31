package com.fatih.runningapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import com.fatih.runningapp.database.RunDao
import com.fatih.runningapp.entities.Run
import timber.log.Timber
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao:RunDao){

    suspend fun insertRun(run: Run){
        try {
            runDao.insertRun(run)
        }catch (e:Exception){
            Timber.e(e)
        }
    }

    suspend fun deleteRun(run:Run){
        try {
            runDao.deleteRun(run)
        }catch (e:Exception){
            Timber.e(e)
        }
    }

    fun getAllRunsSortedByDate(): LiveData<List<Run>> {
        return try {
            runDao.getAllRunsSortedByDate()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>> {
        return try {
            runDao.getAllRunsSortedByTimeInMillis()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
        return try {
            runDao.getAllRunsSortedByDistance()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getAllRunsSortedByAvgKmh(): LiveData<List<Run>> {
        return try {
            runDao.getAllRunsSortedByAvgSpeed()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getAllRunsSortedByCalories(): LiveData<List<Run>> {
        return try {
            runDao.getAllRunsSortedByCaloriesBurned()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getTotalTimeInMillis():LiveData<Long>{
        return try {
            runDao.getTotalTimeInMillis()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getTotalCaloriesBurned():LiveData<Int>{
        return try {
            runDao.getTotalCaloriesBurned()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getTotalDistanceInMeters():LiveData<Int>{
        return try {
            runDao.getTotalDistanceInMeters()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

    fun getTotalAvgSpeedKmh():LiveData<Float>{
        return try {
            runDao.getTotalAvgSpeedKmh()
        }catch (e:Exception){
            Timber.e(e)
            MutableLiveData()
        }
    }

}