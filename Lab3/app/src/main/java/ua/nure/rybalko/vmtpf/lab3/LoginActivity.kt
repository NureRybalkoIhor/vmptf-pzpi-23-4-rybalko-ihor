package ua.nure.rybalko.vmtpf.lab3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)

        findViewById<View>(R.id.btnLoginSubmit).setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Будь ласка, введіть пошту та пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (SessionManager.loginUser(email, password)) {
                Toast.makeText(this, "Вхід виконано успішно", Toast.LENGTH_SHORT).show()
                
                if (intent.getBooleanExtra("redirect_to_checkout", false)) {
                    startActivity(Intent(this, CheckoutActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Невірні облікові дані", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tvGoToRegister).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("redirect_to_checkout", getIntent().getBooleanExtra("redirect_to_checkout", false))
            startActivity(intent)
            finish()
        }
    }
}
