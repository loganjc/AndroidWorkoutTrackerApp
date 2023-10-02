package com.example.workoutapp

import android.app.Application
import androidx.room.RoomDatabase
import com.example.workoutapp.data.WorkoutRoomDatabase
import com.example.workoutapp.data.calories.CaloriesRoomDatabase


class WorkoutApplication : Application() {
    val database: WorkoutRoomDatabase by lazy { WorkoutRoomDatabase.getDatabase(this) }
    val caloriesDatabase: CaloriesRoomDatabase by lazy { CaloriesRoomDatabase.getDatabase(this) }
}