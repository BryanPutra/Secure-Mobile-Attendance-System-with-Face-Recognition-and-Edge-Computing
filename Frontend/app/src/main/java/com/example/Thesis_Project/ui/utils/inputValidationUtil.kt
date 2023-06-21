package com.example.Thesis_Project.ui.utils

import android.util.Patterns
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
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

fun isValidCorrectionRequestDateFrom(date: LocalDate?, attendance: Attendance?): Boolean {
    if (date == null){
        return false
    }
    if (attendance == null) {
        return false
    }
    val attendanceDate = attendance.timein?.let { db_util.dateToLocalDate(it) } ?: return false
    return attendanceDate.isEqual(date)
}

//fun isValidCorrectionRequestDateTo(date: LocalDate, attendance: Attendance): Boolean {
//
//}