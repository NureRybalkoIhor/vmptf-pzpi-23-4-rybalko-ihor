package ua.nure.rybalko.vmtpf.lab3

import java.io.Serializable

data class OrderItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int
) : Serializable

data class Order(
    val id: String,
    val userId: String,
    val userEmail: String,
    val userName: String,
    val items: List<OrderItem>,
    val shippingName: String,
    val shippingAddress: String,
    val shippingPhone: String,
    val totalPrice: Double,
    var status: String,
    val createdAt: String
) : Serializable
