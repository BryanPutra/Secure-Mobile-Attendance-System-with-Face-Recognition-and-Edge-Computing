package com.example.Thesis_Project.ui.navgraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.Thesis_Project.routes.AuthScreenRoutes
import com.example.Thesis_Project.ui.screens.login.LoginAdminScreen
import com.example.Thesis_Project.ui.screens.login.LoginScreen
import com.example.Thesis_Project.ui.screens.login.LoginUserScreen
import com.example.Thesis_Project.viewmodel.MainViewModel

fun NavGraphBuilder.authNavGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    navigation(route = NavGraphs.AUTH, startDestination = AuthScreenRoutes.LoginScreen.route) {
        composable(route = AuthScreenRoutes.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = AuthScreenRoutes.LoginUserScreen.route) {
            LoginUserScreen(navController = navController, mainViewModel)
        }
        composable(route = AuthScreenRoutes.LoginAdminScreen.route) {
            LoginAdminScreen(navController = navController, mainViewModel)
        }
    }
}