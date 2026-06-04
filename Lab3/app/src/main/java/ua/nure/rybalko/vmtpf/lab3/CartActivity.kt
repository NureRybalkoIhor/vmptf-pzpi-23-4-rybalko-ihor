package ua.nure.rybalko.vmtpf.lab3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var tvCartTotalItems: TextView
    private lateinit var tvCartTotalPrice: TextView
    private lateinit var btnClearCart: Button
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvCart = findViewById(R.id.rvCart)
        tvCartTotalItems = findViewById(R.id.tvCartTotalItems)
        tvCartTotalPrice = findViewById(R.id.tvCartTotalPrice)
        btnClearCart = findViewById(R.id.btnClearCart)
        btnCheckout = findViewById(R.id.btnCheckout)

        rvCart.layoutManager = LinearLayoutManager(this)

        setupCartList()

        btnClearCart.setOnClickListener {
            CartManager.clearCart()
            setupCartList()
            Toast.makeText(this, "Кошик очищено", Toast.LENGTH_SHORT).show()
        }

        btnCheckout.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Кошик порожній", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }
    }

    private fun setupCartList() {
        val items = CartManager.getItems()
        adapter = CartAdapter(items) {
            updateCartSummary()
        }
        rvCart.adapter = adapter
        updateCartSummary()
    }

    private fun updateCartSummary() {
        val totalCount = CartManager.getItemsCount()
        val totalPrice = CartManager.getTotalPrice()

        tvCartTotalItems.text = "Товарів: $totalCount"
        tvCartTotalPrice.text = "Сума: $totalPrice грн"
    }
}
