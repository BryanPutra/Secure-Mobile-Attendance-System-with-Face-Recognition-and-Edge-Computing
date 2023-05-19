package com.example.Thesis_Project.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.ui.BottomNavItem
import com.example.Thesis_Project.R

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
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
        items.forEach { item ->
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