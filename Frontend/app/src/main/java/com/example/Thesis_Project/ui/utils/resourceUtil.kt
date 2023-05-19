package com.example.Thesis_Project.ui.utils

import androidx.compose.ui.graphics.Color

fun convertHexToComposeColor (colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}