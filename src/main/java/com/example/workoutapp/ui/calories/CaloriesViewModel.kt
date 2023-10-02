package com.example.workoutapp.ui.calories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CaloriesViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Calories Tracker Input"
    }
    val text: LiveData<String> = _text
}