package com.example.workoutapp.data.calories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaloriesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calories: Calories)

    @Query("SELECT * FROM calories ORDER BY id DESC")
    fun getAll() : Flow<List<Calories>>
}