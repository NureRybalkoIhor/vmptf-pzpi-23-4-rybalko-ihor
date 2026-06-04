package ua.nure.rybalko.vmtpf.pract3

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FitnessActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val workoutsList = mutableListOf<Workout>()
    private lateinit var adapter: FitnessAdapter

    private lateinit var tvStatWorkouts: TextView
    private lateinit var tvStatMinutes: TextView
    private lateinit var tvStatCalories: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitness)

        sharedPreferences = getSharedPreferences("fitness_tracker_prefs", Context.MODE_PRIVATE)

        tvStatWorkouts = findViewById(R.id.tvStatWorkouts)
        tvStatMinutes = findViewById(R.id.tvStatMinutes)
        tvStatCalories = findViewById(R.id.tvStatCalories)

        val etWorkoutType = findViewById<EditText>(R.id.etWorkoutType)
        val etWorkoutDuration = findViewById<EditText>(R.id.etWorkoutDuration)
        val etWorkoutCalories = findViewById<EditText>(R.id.etWorkoutCalories)
        val btnAddWorkout = findViewById<Button>(R.id.btnAddWorkout)

        loadWorkouts()

        val rvWorkouts = findViewById<RecyclerView>(R.id.rvWorkouts)
        rvWorkouts.layoutManager = LinearLayoutManager(this)
        adapter = FitnessAdapter(workoutsList)
        rvWorkouts.adapter = adapter

        updateStats()

        btnAddWorkout.setOnClickListener {
            val type = etWorkoutType.text.toString().trim()
            val durationText = etWorkoutDuration.text.toString().trim()
            val caloriesText = etWorkoutCalories.text.toString().trim()

            if (type.isEmpty() || durationText.isEmpty() || caloriesText.isEmpty()) {
                Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationText.toIntOrNull()
            val calories = caloriesText.toIntOrNull()

            if (duration == null || calories == null || duration <= 0 || calories <= 0) {
                Toast.makeText(this, "Введіть коректні додатні числа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            val newWorkout = Workout(type, duration, calories, currentDate)

            workoutsList.add(0, newWorkout)
            adapter.notifyItemInserted(0)
            rvWorkouts.scrollToPosition(0)

            saveWorkouts()
            updateStats()

            etWorkoutType.text.clear()
            etWorkoutDuration.text.clear()
            etWorkoutCalories.text.clear()
        }
    }

    private fun updateStats() {
        val totalWorkouts = workoutsList.size
        val totalMinutes = workoutsList.sumOf { it.duration }
        val totalCalories = workoutsList.sumOf { it.calories }

        tvStatWorkouts.text = totalWorkouts.toString()
        tvStatMinutes.text = "$totalMinutes хв"
        tvStatCalories.text = "$totalCalories ккал"
    }

    private fun saveWorkouts() {
        val json = gson.toJson(workoutsList)
        sharedPreferences.edit().putString("workout_history", json).apply()
    }

    private fun loadWorkouts() {
        val json = sharedPreferences.getString("workout_history", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Workout>>() {}.type
                val loadedList: List<Workout> = gson.fromJson(json, type)
                workoutsList.clear()
                workoutsList.addAll(loadedList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
