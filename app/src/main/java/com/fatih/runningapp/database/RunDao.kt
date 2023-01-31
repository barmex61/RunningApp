package com.fatih.runningapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fatih.runningapp.entities.Run

@Dao
interface RunDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run:Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM Run ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY avgSpeedKmh DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM Run")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM Run")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM Run")
    fun getTotalDistanceInMeters():LiveData<Int>

    @Query("SELECT SUM(avgSpeedKmh) FROM Run")
    fun getTotalAvgSpeedKmh():LiveData<Float>

}