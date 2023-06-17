package com.example.Thesis_Project.ui.utils

import android.util.Patterns
import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
    val emailRegex = Patterns.EMAIL_ADDRESS
    return emailRegex.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}