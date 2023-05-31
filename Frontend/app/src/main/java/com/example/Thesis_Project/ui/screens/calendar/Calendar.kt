package com.example.Thesis_Project.ui.screens.calendar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.MainHeader

@Composable
fun CalendarScreen(navController: NavController? = null) {
    CalendarContainer(navController)
}

@Composable
fun CalendarContainer(navController: NavController? = null) {

    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val destination = currentBackStackEntry?.destination
    val currentRoute = currentBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = "Bryan Putra",
            onCorrectionSelected = {
            },
            onLeaveSelected = {
            }
        )

    }
}