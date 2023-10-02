package com.example.workoutapp.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.WorkoutDao
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


/*
This class provides access to the Workout DAO and insert methods
 */
class WorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    val allWorkouts: LiveData<List<Workout>> = workoutDao.getAll().asLiveData()

    private fun insertWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    //Creates new workout BD entries based on the inputted values
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNewWorkoutEntry(duration: String, steps: Int, distance: Int) : Workout {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currDate = LocalDate.now().format(formatter)
        return Workout(
            date = currDate.toString(),
            workoutDuration = duration,
            stepCount =  steps,
            distance = distance
        )
    }

    //Function called by UI layer to add stuff to the Database
    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewWorkout(workoutDuration: String, workoutSteps: Int, workoutDistance: Int) {
        val newWorkout = getNewWorkoutEntry(workoutDuration, workoutSteps, workoutDistance)
        insertWorkout(newWorkout)
    }
}

class WorkoutViewModelFactory(private val workoutDao: WorkoutDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(workoutDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class! </3")
    }
}