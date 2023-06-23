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
import com.example.Thesis_Project.ui.screens.camera.RegisterFaceScreen
import com.example.Thesis_Project.ui.screens.camera.TapInScreen
import com.example.Thesis_Project.ui.screens.companyvariable.CompanyVariableScreen
import com.example.Thesis_Project.ui.screens.home.HomeContainer
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun HomeNavGraph(rootNavController: NavHostController, navController: NavHostController, mainViewModel: MainViewModel, onScreenChanged: ((String) -> Unit)? = null){
    NavHost(
        navController = navController,
        route = NavGraphs.HOME,
        startDestination = BottomNavBarRoutes.HomeScreen.route
    ){
        composable(route = BottomNavBarRoutes.HomeScreen.route){
            onScreenChanged?.invoke(BottomNavBarRoutes.HomeScreen.route)
            HomeContainer(rootNavController = rootNavController, navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = BottomNavBarRoutes.CalendarScreen.route) {
            onScreenChanged?.invoke(BottomNavBarRoutes.CalendarScreen.route)
            CalendarScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = BottomNavBarRoutes.HistoryScreen.route) {
            onScreenChanged?.invoke(BottomNavBarRoutes.HistoryScreen.route)
            HistoryScreen(navController = navController, mainViewModel = mainViewModel)
        }
        companyVarNavGraph(navController = navController, mainViewModel = mainViewModel, onScreenChanged = onScreenChanged)
        registerFaceNavGraph(navController = navController, mainViewModel = mainViewModel, onScreenChanged = onScreenChanged)
        tapInNavGraph(navController = navController, mainViewModel = mainViewModel, onScreenChanged = onScreenChanged)
    }
}

fun NavGraphBuilder.companyVarNavGraph(navController: NavHostController, mainViewModel: MainViewModel, onScreenChanged: ((String) -> Unit)? = null){
    navigation(route = NavGraphs.COMPANYVAR, startDestination = HomeSubGraphRoutes.CompanyVarScreen.route){
        composable(route = HomeSubGraphRoutes.CompanyVarScreen.route) {
            onScreenChanged?.invoke(HomeSubGraphRoutes.CompanyVarScreen.route)
            CompanyVariableScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}

fun NavGraphBuilder.registerFaceNavGraph(navController: NavHostController, mainViewModel: MainViewModel, onScreenChanged: ((String) -> Unit)? = null){
    navigation(route = NavGraphs.REGISTERFACE, startDestination = HomeSubGraphRoutes.RegisterFaceScreen.route){
        composable(route = HomeSubGraphRoutes.RegisterFaceScreen.route) {
            onScreenChanged?.invoke(HomeSubGraphRoutes.RegisterFaceScreen.route)
            RegisterFaceScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}

fun NavGraphBuilder.tapInNavGraph(navController: NavHostController, mainViewModel: MainViewModel, onScreenChanged: ((String) -> Unit)? = null){
    navigation(route = NavGraphs.TAPIN, startDestination = HomeSubGraphRoutes.TapInScreen.route){
        composable(route = HomeSubGraphRoutes.TapInScreen.route) {
            onScreenChanged?.invoke(HomeSubGraphRoutes.TapInScreen.route)
            TapInScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}