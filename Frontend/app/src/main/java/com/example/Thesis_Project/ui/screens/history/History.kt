package com.example.Thesis_Project.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.BottomNavItem
import com.example.Thesis_Project.ui.component_item_model.HistoryCardItem
import com.example.Thesis_Project.ui.components.HistoryCard
import com.example.Thesis_Project.ui.components.MainHeader
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme

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
fun HistoryScreen(navController: NavController? = null) {
    HistoryContainer(navController)
}

@Composable
fun HistoryContainer(navController: NavController?) {

    val currentRoute = navController?.currentBackStackEntryAsState()?.value?.destination?.route


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        MainHeader(page = currentRoute, userFullName = "Bryan Putra")
        Box() {
            Text(
                "Correction List",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        LazyColumn { }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        HistoryScreen(
        )
    }
}