package com.example.Thesis_Project.ui.navgraphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.ui.screens.admin.AdminHomeScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun RootNavigationGraph(navController: NavHostController, mainViewModel: MainViewModel) {

//    DisposableEffect(key1 = mainViewModel.currentUser) {
//        if (mainViewModel.currentUser != null) {
//            navController.navigate(NavGraphs.HOME) {
//                popUpTo(NavGraphs.AUTH) { inclusive = true }
//            }
//        }
//        onDispose { }
//    }

    NavHost(
        navController = navController,
        startDestination = NavGraphs.AUTH,
        route = NavGraphs.ROOT
    ) {
        authNavGraph(navController = navController, mainViewModel)
        composable(route = NavGraphs.HOME) {
            HomeScreen(mainViewModel = mainViewModel, rootNavController = navController)
        }
        composable(route = NavGraphs.ADMIN) {
            AdminHomeScreen(mainViewModel = mainViewModel, rootNavController = navController)
        }
    }
}

object NavGraphs {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"
    const val COMPANYVAR = "companyvar_graph"
    const val CAMERA = "camera_graph"
    const val ADMIN = "admin_graph"
}