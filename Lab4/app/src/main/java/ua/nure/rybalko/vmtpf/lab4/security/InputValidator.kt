package ua.nure.rybalko.vmtpf.lab4.security

import java.util.regex.Pattern

object InputValidator {
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    private val PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,15}$"
    )

    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return PHONE_PATTERN.matcher(phone).matches()
    }

    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[';\"#-]"), "").trim()
    }

    fun validateLength(input: String, maxLength: Int): Boolean {
        return input.length <= maxLength
    }
}
