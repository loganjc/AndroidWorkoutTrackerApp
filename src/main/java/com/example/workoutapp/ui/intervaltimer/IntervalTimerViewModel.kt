package com.example.workoutapp.ui.intervaltimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntervalTimerViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is INTERVAL TIMER Fragment"
    }
    val text: LiveData<String> = _text
}