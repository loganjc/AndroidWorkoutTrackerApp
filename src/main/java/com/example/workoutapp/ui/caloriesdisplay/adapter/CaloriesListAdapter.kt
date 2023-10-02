package com.example.workoutapp.ui.caloriesdisplay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.calories.Calories
import com.example.workoutapp.databinding.LayoutCaloriesBinding
import com.example.workoutapp.databinding.LayoutWorkoutBinding

class CaloriesListAdapter : ListAdapter<Calories, CaloriesListAdapter.CaloriesViewHolder>(DiffCallback) {
    class CaloriesViewHolder(private var binding: LayoutCaloriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(calories: Calories) {
            binding.caloriesdisplayTextviewCalories.text = calories.calories
            binding.caloriesdisplayTextviewDate.text = calories.date
            binding.caloriesdisplayTextviewMealname.text = calories.mealName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaloriesViewHolder {
        val viewHolder = CaloriesViewHolder(
            LayoutCaloriesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CaloriesListAdapter.CaloriesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Calories>() {
            override fun areItemsTheSame(oldItem: Calories, newItem: Calories): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Calories, newItem: Calories): Boolean {
                return oldItem == newItem
            }
        }
    }
}