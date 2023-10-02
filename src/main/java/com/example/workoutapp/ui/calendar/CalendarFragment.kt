package com.example.workoutapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.WorkoutApplication
import com.example.workoutapp.data.Workout
import com.example.workoutapp.databinding.FragmentCalendarBinding
import com.example.workoutapp.ui.calendar.recyclerview.WorkoutListAdapter

class CalendarFragment: Fragment() {
    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //RecyclerView Attributes
    private lateinit var recyclerView : RecyclerView
    private val viewModel : WorkoutViewModel by activityViewModels {
        WorkoutViewModelFactory((activity?.application as WorkoutApplication).database.workoutDao())
    }
    lateinit var workout: Workout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calendarViewModel =
            ViewModelProvider(this).get(CalendarViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.calendarRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val workoutAdapter = WorkoutListAdapter()  //codelab has an action in the constructor here I dont think I need it
        recyclerView.adapter = workoutAdapter

        //update recyclerview with database entries
        viewModel.allWorkouts.observe(this.viewLifecycleOwner) {
                workouts -> workouts.let { workoutAdapter.submitList(it) }}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}