package com.example.Thesis_Project

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenRoutes (val route: String, val title: String?, val icon: ImageVector?) {
    object LoginScreen : ScreenRoutes("login_screen", null, null)
    object LoginUserScreen : ScreenRoutes("loginUser_screen", null, null)
    object HomeScreen : ScreenRoutes("home_screen", "Home", Icons.Outlined.Home)
    object CalendarScreen : ScreenRoutes("calendar_screen", "Calendar", Icons.Outlined.CalendarMonth)
    object HistoryScreen : ScreenRoutes("history_screen", "History", Icons.Outlined.WorkHistory)
}