package com.example.Thesis_Project.ui.utils

import androidx.compose.material3.CheckboxColors
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun convertTimeIntToString(time: Int?): String{
    if (time != null){
        val hours = time / 60
        val minutes = time % 60
        return "${hours}h ${minutes}m"
    }
    return ""
}

fun convertHexToComposeColor (colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}
fun formatDateToString (date: Date?): String? {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}
fun formatDateToStringWithDay (date: Date?): String? {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatDateToStringTimeOnly (date: Date?): String? {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatLocalDateToString(date: LocalDate): String{
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM//yyyy", Locale.ENGLISH)
    return date.format(dateFormat)
}
fun formatLocalDateToStringDateOnly(date: LocalDate): String{
    val dateFormat = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH)
    return date.format(dateFormat)
}

fun formatLocalDateToStringDayOnly(date: LocalDate): String{
    val dateFormat = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    return date.format(dateFormat)
}

fun formatMonthYearFromLocalDate (date: LocalDate?): String? {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    if (date != null) {
        return date.format(formatter)
    }
    return ""
}