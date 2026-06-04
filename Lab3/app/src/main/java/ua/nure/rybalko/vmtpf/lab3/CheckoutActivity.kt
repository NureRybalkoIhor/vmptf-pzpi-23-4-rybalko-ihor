package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etCheckoutName: EditText
    private lateinit var etCheckoutAddress: EditText
    private lateinit var etCheckoutPhone: EditText
    private lateinit var tvCheckoutTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val user = SessionManager.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "Будь ласка, авторизуйтесь для оформлення", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("redirect_to_checkout", true)
            startActivity(intent)
            finish()
            return
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        etCheckoutName = findViewById(R.id.etCheckoutName)
        etCheckoutAddress = findViewById(R.id.etCheckoutAddress)
        etCheckoutPhone = findViewById(R.id.etCheckoutPhone)
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal)

        etCheckoutName.setText(user.name)

        val totalPrice = CartManager.getTotalPrice()
        tvCheckoutTotal.text = "Сума: $totalPrice грн"

        findViewById<View>(R.id.btnConfirmOrder).setOnClickListener {
            validateAndConfirmOrder(user)
        }
    }

    private fun validateAndConfirmOrder(user: User) {
        val name = etCheckoutName.text.toString().trim()
        val address = etCheckoutAddress.text.toString().trim()
        val phone = etCheckoutPhone.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Заповніть усі обов'язкові поля", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = CartManager.getItems().map {
            OrderItem(
                productId = it.product.id,
                productName = it.product.name,
                price = it.product.price,
                quantity = it.quantity
            )
        }

        val orderId = "order_" + System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val createdAt = dateFormat.format(Date())

        val newOrder = Order(
            id = orderId,
            userId = user.id,
            userEmail = user.email,
            userName = user.name,
            items = orderItems,
            shippingName = name,
            shippingAddress = address,
            shippingPhone = phone,
            totalPrice = CartManager.getTotalPrice(),
            status = "pending",
            createdAt = createdAt
        )

        saveOrder(newOrder)
        saveCategoryPurchases(user.id, CartManager.getItems().map { it.product.category })

        CartManager.clearCart()

        val intent = Intent(this, OrderConfirmationActivity::class.java)
        intent.putExtra("order_id", orderId)
        startActivity(intent)
        finish()
    }

    private fun saveOrder(order: Order) {
        val prefs = getSharedPreferences("orders_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("orders_list", null)
        
        val type = object : TypeToken<List<Order>>() {}.type
        val orders: MutableList<Order> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        orders.add(order)
        prefs.edit().putString("orders_list", gson.toJson(orders)).apply()
    }

    private fun saveCategoryPurchases(userId: String, categories: List<String>) {
        val prefs = getSharedPreferences("rec_prefs", Context.MODE_PRIVATE)
        val key = "user_${userId}_cats"
        val existing = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        existing.addAll(categories)
        prefs.edit().putStringSet(key, existing).apply()
    }
}
