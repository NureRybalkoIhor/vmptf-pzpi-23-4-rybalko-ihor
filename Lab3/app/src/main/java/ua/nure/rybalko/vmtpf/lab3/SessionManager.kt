package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest

object SessionManager {
    private const val PREF_NAME = "session_prefs"
    private const val KEY_USERS = "users_list"
    private const val KEY_CURRENT_USER = "current_user"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        seedInitialUsers()
    }

    private fun seedInitialUsers() {
        val users = getUsers()
        if (users.none { it.email == "admin@store.com" }) {
            val adminUser = User(
                id = "admin_1",
                name = "Адміністратор",
                email = "admin@store.com",
                passwordHash = hashPassword("admin123"),
                isAdmin = true
            )
            val regularUser = User(
                id = "user_1",
                name = "Ігор Тестовий",
                email = "user@store.com",
                passwordHash = hashPassword("password123"),
                isAdmin = false
            )
            saveUser(adminUser)
            saveUser(regularUser)
        }
    }

    fun getUsers(): List<User> {
        val json = prefs.getString(KEY_USERS, null) ?: return emptyList()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveUser(user: User) {
        val users = getUsers().toMutableList()
        users.removeAll { it.email == user.email }
        users.add(user)
        prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply()
    }

    fun getCurrentUser(): User? {
        val json = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        return gson.fromJson(json, User::class.java)
    }

    fun setCurrentUser(user: User?) {
        if (user == null) {
            prefs.edit().remove(KEY_CURRENT_USER).apply()
        } else {
            prefs.edit().putString(KEY_CURRENT_USER, gson.toJson(user)).apply()
        }
    }

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun registerUser(name: String, email: String, password: String): Boolean {
        val users = getUsers()
        if (users.any { it.email.lowercase() == email.lowercase() }) {
            return false
        }
        val newUser = User(
            id = "user_" + System.currentTimeMillis(),
            name = name,
            email = email,
            passwordHash = hashPassword(password),
            isAdmin = email.lowercase().startsWith("admin@")
        )
        saveUser(newUser)
        setCurrentUser(newUser)
        return true
    }

    fun loginUser(email: String, password: String): Boolean {
        val users = getUsers()
        val targetHash = hashPassword(password)
        val user = users.find { it.email.lowercase() == email.lowercase() && it.passwordHash == targetHash }
        if (user != null) {
            setCurrentUser(user)
            return true
        }
        return false
    }

    fun logout() {
        setCurrentUser(null)
    }
}
