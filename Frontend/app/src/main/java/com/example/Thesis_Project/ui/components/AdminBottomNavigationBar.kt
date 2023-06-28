package com.example.Thesis_Project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.ui.component_item_model.BottomNavItem
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.AdminBottomNavBarRoutes
import com.example.Thesis_Project.routes.BottomNavBarRoutes

private val bottomNavItems =
    listOf(
        BottomNavItem(
            title = "Home",
            route = AdminBottomNavBarRoutes.AdminHomeScreen.route,
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Users",
            route = AdminBottomNavBarRoutes.AdminUsersScreen.route,
            icon = Icons.Outlined.Group
        ),
        BottomNavItem(
            title = "Holidays",
            route = AdminBottomNavBarRoutes.AdminHolidaysScreen.route,
            icon = Icons.Outlined.CalendarMonth
        ),
    )

@Composable
fun AdminBottomNavigationBar(
    navController: NavHostController,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    NavigationBar(
        modifier = Modifier,
        containerColor = colorResource(id = R.color.white),
        tonalElevation = MaterialTheme.elevation.medium,
        contentColor = colorResource(id = R.color.blue_500)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {it.route == item.route} == true
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route){
                    launchSingleTop = true
                    restoreState = true
                } },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = colorResource(id = R.color.blue_500)
                    )
                },
                label = { Text(text = item.title) },
            )
        }
    }
}