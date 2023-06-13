package com.example.Thesis_Project.routes

sealed class HomeSubGraphRoutes (val route: String) {
    object CompanyVarScreen : HomeSubGraphRoutes("companyvar_screen")
    object CameraScreen : HomeSubGraphRoutes("camera_screen")
}