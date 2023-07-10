package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.getUserMonthlyToleranceWorkTime
import com.example.Thesis_Project.viewmodel.MainViewModel


@Composable
fun CheckBackAttendanceDialog(onCloseClicked: () -> Unit) {
    val info: String by remember { mutableStateOf("You forgot to tap out in the previous 14 days, please make a correction request to revert your tolerance work time") }
    Dialog(
        onDismissRequest = { onCloseClicked() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(MaterialTheme.spacing.spaceLarge),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(
                    id = R.color.white
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
        ) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.spaceLarge)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCloseClicked() }, buttonText = "Close")
                    }
                }
            }
        }
    }
}
