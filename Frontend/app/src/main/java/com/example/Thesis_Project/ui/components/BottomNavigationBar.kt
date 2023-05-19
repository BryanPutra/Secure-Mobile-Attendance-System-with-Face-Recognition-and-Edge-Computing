package com.example.Thesis_Project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.Thesis_Project.ScreenRoutes

@Composable
fun BottomNavigationBar(
    items: List<ScreenRoutes>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onRouteClicked: (ScreenRoutes) -> Unit
) {
}