package ua.nure.rybalko.vmtpf.pract3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnGoCalculator).setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoMovies).setOnClickListener {
            startActivity(Intent(this, MoviesActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoFitness).setOnClickListener {
            startActivity(Intent(this, FitnessActivity::class.java))
        }
    }
}
