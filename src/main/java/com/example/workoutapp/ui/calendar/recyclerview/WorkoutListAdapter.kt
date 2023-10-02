package com.example.workoutapp.ui.calendar.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.data.Workout
import com.example.workoutapp.databinding.LayoutWorkoutBinding

class WorkoutListAdapter : ListAdapter<Workout, WorkoutListAdapter.WorkoutViewHolder>(DiffCallback) {
    class WorkoutViewHolder(private var binding: LayoutWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.workoutTextViewDate.text = workout.date
            binding.workoutTextViewDistance.text = workout.distance.toString()
            binding.workoutTextViewDuration.text = workout.workoutDuration
            binding.workoutTextViewSteps.text = workout.stepCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val viewHolder = WorkoutViewHolder(
            LayoutWorkoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            //code lab has a onItemClicked call right here, not needed in this app
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Workout>() {
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem == newItem
            }
        }
    }
}