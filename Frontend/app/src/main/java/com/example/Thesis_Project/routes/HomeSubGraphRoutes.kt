package com.example.Thesis_Project.routes

sealed class HomeSubGraphRoutes (val route: String) {
    object CompanyVarScreen : HomeSubGraphRoutes("companyvar_screen")
    object RegisterFaceScreen : HomeSubGraphRoutes("registerface_screen")
    object TapInScreen : HomeSubGraphRoutes("tapin_screen")

}