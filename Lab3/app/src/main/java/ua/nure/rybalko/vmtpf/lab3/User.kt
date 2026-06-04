package ua.nure.rybalko.vmtpf.lab3

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val isAdmin: Boolean
) : Serializable
