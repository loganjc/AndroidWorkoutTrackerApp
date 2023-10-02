package com.example.workoutapp.ui.caloriesdisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.WorkoutApplication
import com.example.workoutapp.databinding.FragmentCaloriesDisplayBinding
import com.example.workoutapp.ui.calories.CaloriesDatabaseViewModel
import com.example.workoutapp.ui.calories.CaloriesViewModelFactory
import com.example.workoutapp.ui.caloriesdisplay.adapter.CaloriesListAdapter

class CaloriesDisplayFragment : Fragment() {

    private var _binding: FragmentCaloriesDisplayBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val TAG = "CALORIES_DISPLAY_FRAGMENT"
    private lateinit var recyclerView : RecyclerView
    private val viewModel : CaloriesDatabaseViewModel by activityViewModels {
        CaloriesViewModelFactory((activity?.application as WorkoutApplication).caloriesDatabase.caloriesDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val viewModel = ViewModelProvider(this).get(CaloriesDatabaseViewModel::class.java)

        _binding = FragmentCaloriesDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.caloriesDisplayRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val caloriesAdapter = CaloriesListAdapter()
        recyclerView.adapter = caloriesAdapter

        //update recyclerview with database entries
        viewModel.allCalories.observe(this.viewLifecycleOwner) {
                workouts -> workouts.let { caloriesAdapter.submitList(it) }}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}