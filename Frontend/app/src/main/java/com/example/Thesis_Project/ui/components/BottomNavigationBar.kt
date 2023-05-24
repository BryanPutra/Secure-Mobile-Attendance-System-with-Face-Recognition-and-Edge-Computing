package com.example.Thesis_Project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.ui.component_item_model.BottomNavItem
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.BottomNavBarRoutes

val bottomNavItems =
    listOf(
        BottomNavItem(
            title = "Home",
            route = BottomNavBarRoutes.HomeScreen.route,
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Calendar",
            route = BottomNavBarRoutes.CalendarScreen.route,
            icon = Icons.Outlined.CalendarMonth
        ),
        BottomNavItem(
            title = "History",
            route = BottomNavBarRoutes.HistoryScreen.route,
            icon = Icons.Outlined.WorkHistory
        ),
    )

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClicked: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = Modifier,
        containerColor = colorResource(id = R.color.white),
        tonalElevation = MaterialTheme.elevation.medium,
        contentColor = colorResource(id = R.color.blue_500)
    ) {
        bottomNavItems.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected, onClick = { onItemClicked(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                    )
                },
                label = { Text(text = item.title) },
            )
        }
    }
}