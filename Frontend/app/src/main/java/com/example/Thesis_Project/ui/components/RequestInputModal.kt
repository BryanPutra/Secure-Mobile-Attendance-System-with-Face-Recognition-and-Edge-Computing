package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import java.util.*

@Composable
fun RequestInputModal(requestType: String) {

    var dateFrom by remember { mutableStateOf(Date()) }
    var dateTo by remember { mutableStateOf(Date()) }
    var detail by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (requestType == "correction") "Correction Request" else "Leave Request",
            style = MaterialTheme.typography.headlineLarge
        )
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)) {
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(1f),
//                value = dateFrom,
//                onValueChange = { newDateFrom -> dateFrom = newDateFrom })
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(1f),
//                value = dateTo,
//                onValueChange = { newDateTo -> dateTo = newDateTo })
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f).height(120.dp),
                value = detail,
                onValueChange = { newDetail -> detail = newDetail })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        RequestInputModal("leave")
    }
}