package com.example.Thesis_Project.ui.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

fun convertHexToComposeColor (colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}

fun formatDateToString (date: Date): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}