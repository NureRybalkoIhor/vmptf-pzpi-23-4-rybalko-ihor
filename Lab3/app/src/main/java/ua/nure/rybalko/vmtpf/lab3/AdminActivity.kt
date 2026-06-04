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

class AdminActivity : AppCompatActivity() {

    private lateinit var rvAdminOrders: RecyclerView
    private lateinit var adapter: AdminOrderAdapter
    private val ordersList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val user = SessionManager.getCurrentUser()
        if (user == null || !user.isAdmin) {
            Toast.makeText(this, "Доступ дозволено тільки адміністраторам", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvAdminOrders = findViewById(R.id.rvAdminOrders)
        rvAdminOrders.layoutManager = LinearLayoutManager(this)

        loadAndDisplayOrders()
    }

    private fun loadAndDisplayOrders() {
        ordersList.clear()
        ordersList.addAll(loadOrdersFromPrefs())
        ordersList.sortByDescending { it.createdAt }

        adapter = AdminOrderAdapter(ordersList) { order, newStatus ->
            updateOrderStatusInPrefs(order.id, newStatus)
            Toast.makeText(this, "Статус оновлено", Toast.LENGTH_SHORT).show()
        }
        rvAdminOrders.adapter = adapter
    }

    private fun loadOrdersFromPrefs(): List<Order> {
        val prefs = getSharedPreferences("orders_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("orders_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun updateOrderStatusInPrefs(orderId: String, newStatus: String) {
        val prefs = getSharedPreferences("orders_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("orders_list", null) ?: return
        
        val type = object : TypeToken<List<Order>>() {}.type
        val orders: MutableList<Order> = gson.fromJson(json, type)

        val order = orders.find { it.id == orderId }
        if (order != null) {
            order.status = newStatus
            prefs.edit().putString("orders_list", gson.toJson(orders)).apply()
        }
    }
}
