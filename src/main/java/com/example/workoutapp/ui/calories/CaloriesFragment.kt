package com.example.workoutapp.ui.calories

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workoutapp.WorkoutApplication
import com.example.workoutapp.databinding.FragmentCaloriesBinding
import com.example.workoutapp.ui.calendar.WorkoutViewModel
import com.example.workoutapp.ui.calendar.WorkoutViewModelFactory

class CaloriesFragment: Fragment() {
    private var _binding: FragmentCaloriesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val TAG = "CALORIES_INPUT_FRAGMENT"
    private lateinit var caloriesTextView: EditText
    private lateinit var mealNameTextView: EditText
    private val viewModel : CaloriesDatabaseViewModel by activityViewModels {
        CaloriesViewModelFactory((activity?.application as WorkoutApplication).caloriesDatabase.caloriesDao())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val caloriesViewModel =
            ViewModelProvider(this).get(CaloriesViewModel::class.java)

        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textView4
        caloriesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        mealNameTextView = binding.editTextMealName
        caloriesTextView = binding.editTextCalories
        binding.buttonRecordCalories.setOnClickListener{
            recordData()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun recordData() {
        //if fields are not empty, save data.
        if (!caloriesTextView.text.equals("") && !mealNameTextView.text.equals("")) {
            viewModel.addNewCalories(mealNameTextView.text.toString(), caloriesTextView.text.toString())
            Log.d(TAG, "Calorie data saved!")
            caloriesTextView.text.clear()
            mealNameTextView.text.clear()
        }
    }
}
