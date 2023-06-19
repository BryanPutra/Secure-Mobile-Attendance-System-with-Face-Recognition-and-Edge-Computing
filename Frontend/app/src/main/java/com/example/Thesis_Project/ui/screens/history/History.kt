package com.example.Thesis_Project.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.HistoryCardItem
import com.example.Thesis_Project.ui.components.CorrectionRequestCard
import com.example.Thesis_Project.ui.components.LeaveRequestCard
import com.example.Thesis_Project.ui.components.MainHeader
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun HistoryScreen(navController: NavController, mainViewModel: MainViewModel) {
    HistoryContainer(navController, mainViewModel)
}

@Composable
fun HistoryContainer(navController: NavController, mainViewModel: MainViewModel) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    var isLaunched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (!isLaunched) {
            db_util.getLeaveRequest(
                mainViewModel.db,
                mainViewModel.userData?.userid,
                mainViewModel.setLeaveRequestList
            )
            db_util.getCorrectionRequest(
                mainViewModel.db,
                mainViewModel.userData?.userid,
                mainViewModel.setCorrectionRequestList
            )
            isLaunched = true
        }
    }

    LaunchedEffect(mainViewModel.userData) {
        db_util.getLeaveRequest(
            mainViewModel.db,
            mainViewModel.userData?.userid,
            mainViewModel.setLeaveRequestList
        )
        db_util.getCorrectionRequest(
            mainViewModel.db,
            mainViewModel.userData?.userid,
            mainViewModel.setCorrectionRequestList
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = mainViewModel.userData?.name,
            correctionSelected = mainViewModel.correctionSelected,
            leaveSelected = mainViewModel.leaveSelected,
            switchTabs = mainViewModel.switchHistoryTab,
            mainViewModel = mainViewModel
        )
        if (mainViewModel.correctionSelected) {
            if (mainViewModel.correctionRequestList?.isEmpty() == true) {
                LazyColumn(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Correction List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    items(mainViewModel.correctionRequestList!!) { correctionCardItem ->
                        CorrectionRequestCard(correctionCardItem)
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Correction List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    item {
                        Text(
                            "No Correction Request List Found",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        } else {
            if (mainViewModel.leaveRequestList?.isEmpty() == true) {
                LazyColumn(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Leave List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    items(mainViewModel.leaveRequestList!!) { leaveCardItem ->
                        LeaveRequestCard(leaveRequest = leaveCardItem)
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Leave List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    item {
                        Text(
                            "No Leave Request List Found",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
    }
}