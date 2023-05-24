package com.example.Thesis_Project.routes

sealed class BottomNavBarRoutes(val route: String){
    object HomeScreen : BottomNavBarRoutes("home_screen")
    object CalendarScreen : BottomNavBarRoutes("calendar_screen")
    object HistoryScreen : BottomNavBarRoutes("history_screen")
}
