package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

data class CartItem(val product: Product, var quantity: Int) : Serializable

object CartManager {
    private const val PREF_NAME = "cart_prefs"
    private const val KEY_ITEMS = "cart_items"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private val cartItems = mutableListOf<CartItem>()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadCart()
    }

    private fun loadCart() {
        cartItems.clear()
        val json = prefs.getString(KEY_ITEMS, null) ?: return
        val type = object : TypeToken<List<CartItem>>() {}.type
        val items: List<CartItem> = gson.fromJson(json, type)
        cartItems.addAll(items)
    }

    private fun saveCart() {
        prefs.edit().putString(KEY_ITEMS, gson.toJson(cartItems)).apply()
    }

    fun getItems(): List<CartItem> = cartItems

    fun addProduct(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(product, 1))
        }
        saveCart()
    }

    fun removeProduct(product: Product) {
        cartItems.removeAll { it.product.id == product.id }
        saveCart()
    }

    fun decrementProduct(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity--
            if (existing.quantity <= 0) {
                cartItems.remove(existing)
            }
        }
        saveCart()
    }

    fun clearCart() {
        cartItems.clear()
        saveCart()
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.product.price * it.quantity }
    }

    fun getItemsCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun isInCart(productId: String): Boolean {
        return cartItems.any { it.product.id == productId }
    }
}
