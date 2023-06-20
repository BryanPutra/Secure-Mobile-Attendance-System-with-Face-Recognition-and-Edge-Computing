package com.example.Thesis_Project.ui.utils

import android.util.Patterns
import java.time.LocalDate

fun isValidEmail(email: String): Boolean {
    val emailRegex = Patterns.EMAIL_ADDRESS
    return emailRegex.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

fun isValidName(name: String): Boolean {
    return name.length >= 3
}

fun isValidLeaveRequestDateFrom(date: LocalDate): Boolean {
    val currentDate = LocalDate.now()
    return date.isAfter(currentDate) || date.isEqual(currentDate)
}

fun isValidLeaveRequestDateTo(dateFrom: LocalDate, dateTo: LocalDate): Boolean {
    return dateTo.isAfter(dateFrom) || dateTo.isEqual(dateFrom)
}