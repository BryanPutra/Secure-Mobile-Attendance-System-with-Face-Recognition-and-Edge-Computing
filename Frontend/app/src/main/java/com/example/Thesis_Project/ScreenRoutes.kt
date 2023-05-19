package com.example.Thesis_Project

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenRoutes (val route: String) {
    object LoginScreen : ScreenRoutes("login_screen")
    object LoginUserScreen : ScreenRoutes("loginUser_screen")
    object HomeScreen : ScreenRoutes("home_screen")
    object CalendarScreen : ScreenRoutes("calendar_screen")
    object HistoryScreen : ScreenRoutes("history_screen")
}