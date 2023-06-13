package com.example.Thesis_Project.ui.navgraphs

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun RootNavigationGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavGraphs.AUTH,
        route = NavGraphs.ROOT
    ) {
        authNavGraph(navController = navController)
        composable(route = NavGraphs.HOME) {
            HomeScreen(mainViewModel = mainViewModel)
        }
    }
}

object NavGraphs {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"
    const val COMPANYVAR = "companyvar_graph"
    const val CAMERA = "camera_graph"
}