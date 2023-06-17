package com.example.Thesis_Project.ui.navgraphs

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.ui.screens.admin.AdminHomeScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.viewmodel.AdminViewModel
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun RootNavigationGraph(navController: NavHostController, mainViewModel: MainViewModel, adminViewModel: AdminViewModel) {
    Log.d("Print user auth", "isAuthenticated: ${mainViewModel.checkAuth()}")
    fun setStartDestination(): String {
        if (!mainViewModel.checkAuth()) return NavGraphs.AUTH
        if (mainViewModel.isUserAdmin) return NavGraphs.ADMIN
        return NavGraphs.HOME
    }
    NavHost(
        navController = navController,
        startDestination = setStartDestination(),
        route = NavGraphs.ROOT
    ) {
        authNavGraph(navController = navController, mainViewModel)
        composable(route = NavGraphs.HOME) {
            HomeScreen(mainViewModel = mainViewModel)
        }
        composable(route = NavGraphs.ADMIN) {
            AdminHomeScreen(adminViewModel = adminViewModel)
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