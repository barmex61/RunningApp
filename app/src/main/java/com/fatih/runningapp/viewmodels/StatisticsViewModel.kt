package com.fatih.runningapp.viewmodels

import androidx.lifecycle.ViewModel
import com.fatih.runningapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val mainRepository: MainRepository) :ViewModel(){

    val totalTimeRun = mainRepository.getTotalTimeInMillis()
    val totalDistance = mainRepository.getTotalDistanceInMeters()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed = mainRepository.getTotalAvgSpeedKmh()
    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()

}