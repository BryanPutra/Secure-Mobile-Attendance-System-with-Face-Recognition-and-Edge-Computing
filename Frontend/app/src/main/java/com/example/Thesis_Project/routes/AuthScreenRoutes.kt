package com.example.Thesis_Project.routes

sealed class AuthScreenRoutes(val route: String){
    object LoginScreen : AuthScreenRoutes("login_screen")
    object LoginUserScreen : AuthScreenRoutes("loginUser_screen")
}
