package com.example.workoutapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

/*
Class represents a database entry
 */

@Entity(tableName = "workout")
data class Workout (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "workoutDuration")
    val workoutDuration: String,
    @ColumnInfo(name = "stepCount")
    val stepCount: Int,
    @ColumnInfo(name = "distance")
    val distance: Int
)