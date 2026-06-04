package ua.nure.rybalko.vmtpf.pract3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val etNum1 = findViewById<EditText>(R.id.etNum1)
        val etNum2 = findViewById<EditText>(R.id.etNum2)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSubtract = findViewById<Button>(R.id.btnSubtract)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)
        val btnDivide = findViewById<Button>(R.id.btnDivide)

        fun calculate(operation: (Double, Double) -> Double, isDivision: Boolean = false) {
            val num1 = etNum1.text.toString().toDoubleOrNull()
            val num2 = etNum2.text.toString().toDoubleOrNull()

            if (num1 == null || num2 == null) {
                Toast.makeText(this, "Будь ласка, введіть обидва числа", Toast.LENGTH_SHORT).show()
                return
            }

            if (isDivision && num2 == 0.0) {
                tvResult.text = "Результат: Помилка (ділення на 0)"
                Toast.makeText(this, "Ділення на нуль неможливе", Toast.LENGTH_SHORT).show()
                return
            }

            val res = operation(num1, num2)
            tvResult.text = "Результат: $res"
        }

        btnAdd.setOnClickListener { calculate({ a, b -> a + b }) }
        btnSubtract.setOnClickListener { calculate({ a, b -> a - b }) }
        btnMultiply.setOnClickListener { calculate({ a, b -> a * b }) }
        btnDivide.setOnClickListener { calculate({ a, b -> a / b }, true) }
    }
}
