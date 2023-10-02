package com.example.workoutapp.data.calories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calories")
data class Calories (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name ="date")
    val date : String,
    @ColumnInfo(name = "meal_name")
    val mealName: String,
    @ColumnInfo(name = "calories")
    val calories: String
)