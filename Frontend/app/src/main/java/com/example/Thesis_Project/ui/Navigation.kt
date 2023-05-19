package com.example.Thesis_Project.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ScreenRoutes
import com.example.Thesis_Project.ui.screens.calendar.CalendarScreen
import com.example.Thesis_Project.ui.screens.history.HistoryScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.ui.screens.login.LoginScreen
import com.example.Thesis_Project.ui.screens.login.LoginUserScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = ScreenRoutes.HomeScreen.route
    ){
        composable(route = ScreenRoutes.LoginScreen.route){
            LoginScreen(navController = navController)
        }
        composable(route = ScreenRoutes.LoginUserScreen.route){
            LoginUserScreen(navController = navController)
        }
        composable(route = ScreenRoutes.HomeScreen.route){
            HomeScreen(navController = navController)
        }
        composable(route = ScreenRoutes.CalendarScreen.route){
            CalendarScreen(navController = navController)
        }
        composable(route = ScreenRoutes.HistoryScreen.route){
            HistoryScreen(navController = navController)
        }
    }
}
