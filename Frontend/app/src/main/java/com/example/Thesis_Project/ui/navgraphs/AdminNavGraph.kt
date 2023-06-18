package com.example.Thesis_Project.ui.navgraphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.routes.AdminBottomNavBarRoutes
import com.example.Thesis_Project.ui.screens.admin.AdminHomeContainer
import com.example.Thesis_Project.ui.screens.admin.AdminHomeScreen
import com.example.Thesis_Project.ui.screens.admin.AdminUsersScreen
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminNavGraph(rootNavController: NavHostController, navController: NavHostController, mainViewModel: MainViewModel){
    NavHost(
        navController = navController,
        route = NavGraphs.ADMIN,
        startDestination = AdminBottomNavBarRoutes.AdminHomeScreen.route
    ){
        composable(route = AdminBottomNavBarRoutes.AdminHomeScreen.route){
            AdminHomeContainer(rootNavController, navController, mainViewModel)
        }
        composable(route = AdminBottomNavBarRoutes.AdminUsersScreen.route) {
            AdminUsersScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}