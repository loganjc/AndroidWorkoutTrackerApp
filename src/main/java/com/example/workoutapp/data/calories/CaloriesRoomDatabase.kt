package com.example.workoutapp.data.calories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities= [Calories::class], version =1, exportSchema = false)
abstract class CaloriesRoomDatabase : RoomDatabase() {

    abstract fun caloriesDao(): CaloriesDao

    companion object {
        @Volatile
        private var INSTANCE: CaloriesRoomDatabase? = null
        fun getDatabase(context: Context): CaloriesRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaloriesRoomDatabase::class.java,
                    "calories_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}