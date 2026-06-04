package ua.nure.rybalko.vmtpf.pract3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FitnessAdapter(private val workoutList: List<Workout>) : 
    RecyclerView.Adapter<FitnessAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWorkoutType: TextView = view.findViewById(R.id.tvWorkoutType)
        val tvWorkoutDate: TextView = view.findViewById(R.id.tvWorkoutDate)
        val tvWorkoutStats: TextView = view.findViewById(R.id.tvWorkoutStats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.tvWorkoutType.text = workout.type
        holder.tvWorkoutDate.text = workout.date
        holder.tvWorkoutStats.text = "${workout.duration} хв | ${workout.calories} ккал"
    }

    override fun getItemCount(): Int = workoutList.size
}
