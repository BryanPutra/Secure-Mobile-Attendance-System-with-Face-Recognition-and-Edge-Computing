package com.example.Thesis_Project.ui.navgraphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import com.example.Thesis_Project.ui.screens.calendar.CalendarScreen
import com.example.Thesis_Project.ui.screens.history.HistoryScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.routes.HomeSubGraphRoutes
import com.example.Thesis_Project.ui.screens.companyvariable.CompanyVariableScreen
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
        companyVarNavGraph(navController = navController, mainViewModel = mainViewModel)
    }
}

fun NavGraphBuilder.companyVarNavGraph(navController: NavHostController, mainViewModel: MainViewModel){
    navigation(route = NavGraphs.COMPANYVAR, startDestination = HomeSubGraphRoutes.CompanyVarScreen.route){
        composable(route = HomeSubGraphRoutes.CompanyVarScreen.route) {
            CompanyVariableScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}