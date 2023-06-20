package com.example.Thesis_Project.routes

sealed class AdminBottomNavBarRoutes(val route: String) {
    object AdminHomeScreen : AdminBottomNavBarRoutes("admin-home_screen")
    object AdminUsersScreen : AdminBottomNavBarRoutes("admin-users_screen")
}
