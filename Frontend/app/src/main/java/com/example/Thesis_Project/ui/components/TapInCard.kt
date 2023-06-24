package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.checkDateIsWeekend
import com.example.Thesis_Project.ui.utils.replaceTimeInDate
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TapInCard(navController: NavController, mainViewModel: MainViewModel) {

    var currentDateTime by rememberSaveable { mutableStateOf(Date()) }
    val companyTimeInTime by rememberSaveable { mutableStateOf(replaceTimeInDate(currentDateTime, mainViewModel.companyVariable?.tapintime))}
    val companyTimeOutTime by rememberSaveable { mutableStateOf(replaceTimeInDate(currentDateTime, mainViewModel.companyVariable?.tapouttime))}

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = Date()
            if (mainViewModel.todayAttendance?.timeout == null && currentDateTime >= companyTimeInTime && currentDateTime <= companyTimeOutTime && !checkDateIsWeekend(currentDateTime)){
                mainViewModel.setTapInDisabled(true)
            }
            else {
                mainViewModel.setTapInDisabled(false)
            }
            delay(1000) // Delay for 1 minute (60000 milliseconds)
        }
    }

    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    val timeFormat = SimpleDateFormat("HH : mm", Locale.ENGLISH)
    val formattedDate = dateFormat.format(currentDateTime)
    val formattedTime = timeFormat.format(currentDateTime)

    fun onTapIn() {
        navController.navigate(NavGraphs.TAPIN)
    }

    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = colorResource(
                id = R.color.white
            )
        ), elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.spaceLarge,
                alignment = Alignment.CenterVertically
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceSmall,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Today, contentDescription = null,
                    tint = colorResource(
                        id = R.color.blue_500
                    ),
                    modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                )
                Text(text = formattedDate, style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.headlineLarge,
                color = colorResource(id = R.color.blue_500)
            )
            ButtonHalfWidth(onClick = { onTapIn() }, buttonText = "Tap In", isEnabled = mainViewModel.tapInDisabled)
        }
    }
}