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
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun HistoryScreen(navController: NavController, mainViewModel: MainViewModel) {
    HistoryContainer(navController, mainViewModel)
}

@Composable
fun HistoryContainer(navController: NavController, mainViewModel: MainViewModel) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route
    val isLaunched by rememberSaveable { mutableStateOf(mainViewModel.isHistoryInit) }
    var selectedViewLeaveRequest by remember { mutableStateOf<LeaveRequest?>(null) }
    var selectedViewCorrectionRequest by remember { mutableStateOf<CorrectionRequest?>(null) }

    suspend fun getInitData() {
        coroutineScope {
            launch {
                db_util.getLeaveRequest(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    mainViewModel.setLeaveRequestList
                )
            }
            launch {
                db_util.getCorrectionRequest(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    mainViewModel.setCorrectionRequestList
                )
            }
        }
    }

    LaunchedEffect(key1 = isLaunched) {
        if (!isLaunched) {
            runBlocking {
                getInitData()
                mainViewModel.setIsHistoryInit(true)
            }
        }
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
            if (mainViewModel.correctionRequestList?.isEmpty() == false) {
                LazyColumn(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.spaceMedium,
                        vertical = MaterialTheme.spacing.spaceLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Correction List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    items(mainViewModel.correctionRequestList!!) { correctionCardItem ->
                        CorrectionRequestCard(correctionCardItem){ correctionRequestItem ->
                            selectedViewCorrectionRequest = correctionRequestItem
                            mainViewModel.toggleCancelCorrectionDialog()
                        }
                    }
                }
            } else {
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
            if (mainViewModel.leaveRequestList?.isEmpty() == false) {
                LazyColumn(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium).padding(bottom = MaterialTheme.spacing.spaceExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
                ) {
                    item {
                        Text(
                            "Leave List",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    items(mainViewModel.leaveRequestList!!) { leaveCardItem ->
                        LeaveRequestCard(
                            leaveRequest = leaveCardItem,
                        ) { leaveRequestItem ->
                            selectedViewLeaveRequest = leaveRequestItem
                            mainViewModel.toggleCancelLeaveDialog()
                        }
                    }
                }
            } else {
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
        if (mainViewModel.isCancelLeaveDialogShown) {
            CancelLeaveDialog(selectedViewLeaveRequest, mainViewModel) {
                mainViewModel.toggleCancelLeaveDialog()
                selectedViewLeaveRequest = null
            }
        }
        if (mainViewModel.isCancelCorrectionDialogShown) {
            CancelCorrectionDialog(selectedViewCorrectionRequest, mainViewModel) {
                mainViewModel.toggleCancelCorrectionDialog()
                selectedViewCorrectionRequest = null
            }
        }
    }
}