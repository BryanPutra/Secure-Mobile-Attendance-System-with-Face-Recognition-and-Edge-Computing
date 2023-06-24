package com.example.Thesis_Project.ui.navgraphs

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Thesis_Project.ui.screens.admin.AdminHomeScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.ui.utils.timeStringFromLong
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CompletableDeferred
import java.util.*

@Composable
fun RootNavigationGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    val isLaunched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mainViewModel.currentUser) {
        if (!isLaunched) {
            val authStateListenerCompleted = CompletableDeferred<Unit>()
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                mainViewModel.setCurrentUser(firebaseAuth.currentUser)
                authStateListenerCompleted.complete(Unit)
            }
            mainViewModel.auth.addAuthStateListener(authStateListener)
            try {
                authStateListenerCompleted.await()
                if (mainViewModel.currentUser != null) {
                    navController.navigate(NavGraphs.HOME) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                }
            } finally {
                mainViewModel.auth.removeAuthStateListener(authStateListener)
            }
        }

    }

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
    const val REGISTERFACE = "registerface_graph"
    const val TAPIN = "tapin_graph"
    const val ADMIN = "admin_graph"
}