package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrdersActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val user = SessionManager.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "Авторизуйтесь, щоб переглянути замовлення", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvOrders = findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        setupOrdersList(user.id)
    }

    private fun setupOrdersList(userId: String) {
        val allOrders = loadOrdersFromPrefs()
        val userOrders = allOrders.filter { it.userId == userId }.sortedByDescending { it.createdAt }

        adapter = OrderAdapter(userOrders)
        rvOrders.adapter = adapter
    }

    private fun loadOrdersFromPrefs(): List<Order> {
        val prefs = getSharedPreferences("orders_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("orders_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return Gson().fromJson(json, type)
    }
}
