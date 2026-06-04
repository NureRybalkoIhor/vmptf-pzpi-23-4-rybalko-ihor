package ua.nure.rybalko.vmtpf.lab3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegisterName: EditText
    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etRegisterName = findViewById(R.id.etRegisterName)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)

        findViewById<View>(R.id.btnRegisterSubmit).setOnClickListener {
            val name = etRegisterName.text.toString().trim()
            val email = etRegisterEmail.text.toString().trim()
            val password = etRegisterPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заповніть усі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (SessionManager.registerUser(name, email, password)) {
                Toast.makeText(this, "Реєстрація успішна", Toast.LENGTH_SHORT).show()
                
                if (intent.getBooleanExtra("redirect_to_checkout", false)) {
                    startActivity(Intent(this, CheckoutActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Користувач з цією поштою вже існує", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tvGoToLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("redirect_to_checkout", getIntent().getBooleanExtra("redirect_to_checkout", false))
            startActivity(intent)
            finish()
        }
    }
}
