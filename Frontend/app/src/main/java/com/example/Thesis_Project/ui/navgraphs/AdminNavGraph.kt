package com.example.Thesis_Project.ui.navgraphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.routes.AdminBottomNavBarRoutes
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.ui.screens.admin.AdminHomeScreen
import com.example.Thesis_Project.ui.screens.admin.AdminUsersScreen
import com.example.Thesis_Project.ui.screens.calendar.CalendarScreen
import com.example.Thesis_Project.ui.screens.history.HistoryScreen
import com.example.Thesis_Project.ui.screens.home.HomeContainer
import com.example.Thesis_Project.viewmodel.AdminViewModel
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminNavGraph(navController: NavHostController, adminViewModel: AdminViewModel){
    NavHost(
        navController = navController,
        route = NavGraphs.ADMIN,
        startDestination = AdminBottomNavBarRoutes.AdminHomeScreen.route
    ){
        composable(route = AdminBottomNavBarRoutes.AdminHomeScreen.route){
            AdminHomeScreen(navController = navController, adminViewModel = adminViewModel)
        }
        composable(route = AdminBottomNavBarRoutes.AdminUsersScreen.route) {
            AdminUsersScreen(navController = navController, adminViewModel = adminViewModel)
        }
    }
}