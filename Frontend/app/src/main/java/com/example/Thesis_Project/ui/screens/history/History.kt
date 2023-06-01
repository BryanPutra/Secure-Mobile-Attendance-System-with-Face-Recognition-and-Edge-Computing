package com.example.Thesis_Project.ui.screens.history

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.HistoryCardItem
import com.example.Thesis_Project.ui.components.HistoryCard
import com.example.Thesis_Project.ui.components.MainHeader

val correctionCardItems =
    listOf(
        HistoryCardItem(
            historyType = "Corrrection",
            reason = "I forgot to tap in when i went into the office",
            dateGenerated = "Friday, 3 March 2023",
            status = "Approved",
        ),
        HistoryCardItem(
            historyType = "Corrrection",
            reason = "Forgot to tap in.....",
            dateGenerated = "Friday, 24 June 2023",
            status = "Pending",
        ),
        HistoryCardItem(
            historyType = "Corrrection",
            reason = "I forgot to tap out at this date",
            dateGenerated = "Friday, 3 March 2023",
            status = "Approved",
        ),
    )

val leaveCardItems = listOf(
    HistoryCardItem(
        historyType = "Leave",
        reason = "I have to attend to a relative’s wedding party in a different city i went into the office",
        dateGenerated = "Friday, 3 March 2023",
        status = "Approved",
    ),
    HistoryCardItem(
        historyType = "Leave",
        reason = "Lebaran bos",
        dateGenerated = "Friday, 24 June 2023",
        status = "Approved",
    ),
    HistoryCardItem(
        historyType = "Leave",
        reason = "I have to attend to a relative’s wedding party in a different city",
        dateGenerated = "Friday, 3 March 2023",
        status = "Pending",
    ),
)

@Composable
fun HistoryScreen(navController: NavController) {
    HistoryContainer(navController)
}

@Composable
fun HistoryContainer(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route
    val correctionSelected = remember { mutableStateOf(true) };
    val leaveSelected = remember { mutableStateOf(false) };

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = "Bryan Putra",
            correctionSelected = correctionSelected.value,
            leaveSelected = leaveSelected.value,
            onCorrectionSelected = { newCorrectionState ->
                correctionSelected.value = newCorrectionState
            },
            onLeaveSelected = { newLeaveState ->
                leaveSelected.value = newLeaveState
            }
        )
        if (correctionSelected.value) {
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
                items(correctionCardItems) { correctionCardItem ->
                    HistoryCard(
                        historyType = correctionCardItem.historyType,
                        description = correctionCardItem.reason,
                        date = correctionCardItem.dateGenerated,
                        status = correctionCardItem.status
                    )
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
                items(leaveCardItems) { leaveCardItem ->
                    HistoryCard(
                        historyType = leaveCardItem.historyType,
                        description = leaveCardItem.reason,
                        date = leaveCardItem.dateGenerated,
                        status = leaveCardItem.status
                    )
                }
            }
        }
    }
}