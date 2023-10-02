package com.example.workoutapp.ui.calories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.WorkoutDao
import com.example.workoutapp.data.calories.Calories
import com.example.workoutapp.data.calories.CaloriesDao
import com.example.workoutapp.ui.calendar.WorkoutViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CaloriesDatabaseViewModel(private val caloriesDao: CaloriesDao) : ViewModel() {

    val allCalories: LiveData<List<Calories>> = caloriesDao.getAll().asLiveData()

    private fun insertCalories(calories: Calories) {
        viewModelScope.launch {
            caloriesDao.insert(calories)
        }
    }

    //Creates new workout BD entries based on the inputted values
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNewCaloriesEntry(meal: String, cals: String) : Calories {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currDate = LocalDate.now().format(formatter)
        return Calories(
            date = currDate.toString(),
            mealName = meal,
            calories = cals
        )
    }

    //Function called by UI layer to add stuff to the Database
    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewCalories(mealName: String, calories: String) {
        val newCalories = getNewCaloriesEntry(mealName, calories)
        insertCalories(newCalories)
    }
}

class CaloriesViewModelFactory(private val caloriesDao: CaloriesDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaloriesDatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaloriesDatabaseViewModel(caloriesDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class! </3")
    }

}