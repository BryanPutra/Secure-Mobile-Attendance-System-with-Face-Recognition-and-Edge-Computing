package com.example.Thesis_Project.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ScreenRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.BottomNavigationBar
import com.example.Thesis_Project.ui.screens.calendar.CalendarScreen
import com.example.Thesis_Project.ui.screens.history.HistoryScreen
import com.example.Thesis_Project.ui.screens.home.HomeScreen
import com.example.Thesis_Project.ui.screens.login.LoginScreen
import com.example.Thesis_Project.ui.screens.login.LoginUserScreen

val bottomNavItems =
    listOf(
        BottomNavItem(
            title = "Home",
            route = ScreenRoutes.HomeScreen.route,
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Calendar",
            route = ScreenRoutes.CalendarScreen.route,
            icon = Icons.Outlined.CalendarMonth
        ),
        BottomNavItem(
            title = "History",
            route = ScreenRoutes.HistoryScreen.route,
            icon = Icons.Outlined.WorkHistory
        ),
    )

@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        BottomNavigationBar(
            items = bottomNavItems,
            navController = navController,
            onItemClicked = { navController.navigate(it.route) }
        )
    }, content = { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController, startDestination = ScreenRoutes.HomeScreen.route
            ) {
                composable(route = ScreenRoutes.LoginScreen.route) {
                    LoginScreen(navController = navController)
                }
                composable(route = ScreenRoutes.LoginUserScreen.route) {
                    LoginUserScreen(navController = navController)
                }
                composable(route = ScreenRoutes.HomeScreen.route) {
                    HomeScreen(navController = navController)
                }
                composable(route = ScreenRoutes.CalendarScreen.route) {
                    CalendarScreen(navController = navController)
                }
                composable(route = ScreenRoutes.HistoryScreen.route) {
                    HistoryScreen(navController = navController)
                }
            }
        }

    }

    )

}
