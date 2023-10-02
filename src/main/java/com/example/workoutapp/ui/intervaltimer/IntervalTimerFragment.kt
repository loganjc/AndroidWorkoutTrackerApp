package com.example.workoutapp.ui.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workoutapp.databinding.FragmentIntervalTimerBinding
import com.example.workoutapp.ui.calories.CaloriesViewModel

class IntervalTimerFragment: Fragment() {
    private var _binding: FragmentIntervalTimerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val intervalTimerViewModel =
            ViewModelProvider(this).get(IntervalTimerViewModel::class.java)

        _binding = FragmentIntervalTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textView3
        intervalTimerViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}