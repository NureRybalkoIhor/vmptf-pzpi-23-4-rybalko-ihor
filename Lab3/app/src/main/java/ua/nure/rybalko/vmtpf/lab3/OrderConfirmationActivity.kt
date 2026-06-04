package ua.nure.rybalko.vmtpf.lab3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val orderId = intent.getStringExtra("order_id") ?: "-"

        findViewById<TextView>(R.id.tvConfirmationOrderId).text = "Номер: $orderId"
        findViewById<TextView>(R.id.tvConfirmationStatus).text = "Статус: Очікує"

        findViewById<View>(R.id.btnConfirmationDone).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
