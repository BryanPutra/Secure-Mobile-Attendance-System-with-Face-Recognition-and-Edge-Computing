package com.example.Thesis_Project.ui.navgraphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.example.Thesis_Project.ui.screens.calendar.CalendarScreen
import com.example.Thesis_Project.ui.screens.history.HistoryScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.ui.screens.home.HomeContainer
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun HomeNavGraph(navController: NavHostController, mainViewModel: MainViewModel){
    NavHost(
        navController = navController,
        route = NavGraphs.HOME,
        startDestination = BottomNavBarRoutes.HomeScreen.route
    ){
        composable(route = BottomNavBarRoutes.HomeScreen.route){
            HomeContainer(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = BottomNavBarRoutes.CalendarScreen.route) {
            CalendarScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = BottomNavBarRoutes.HistoryScreen.route) {
            HistoryScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}