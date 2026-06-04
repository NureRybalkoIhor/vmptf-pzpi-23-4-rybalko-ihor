package ua.nure.rybalko.vmtpf.lab3

import java.io.Serializable

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String,
    val image: String,
    var isFavorite: Boolean = false
) : Serializable
