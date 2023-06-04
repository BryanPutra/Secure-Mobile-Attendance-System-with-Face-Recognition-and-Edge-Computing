package com.example.Thesis_Project.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun convertHexToComposeColor (colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}
fun formatDateToString (date: Date): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun formatMonthYearFromDate (date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    return date.format(formatter)
}