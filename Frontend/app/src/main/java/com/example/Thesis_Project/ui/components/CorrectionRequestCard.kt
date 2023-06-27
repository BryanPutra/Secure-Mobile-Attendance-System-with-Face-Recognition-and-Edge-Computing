package com.example.Thesis_Project.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToString

@Composable
fun CorrectionRequestCard(correctionRequest: CorrectionRequest?, onViewClick: (CorrectionRequest) -> Unit) {
    val correctionTitle: String = "Correction Request";

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight().clickable {
                    if (correctionRequest?.approvedflag == null || correctionRequest.approvedflag == false) {
                        onViewClick(correctionRequest!!)
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = colorResource(
                    id = R.color.white
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceLarge,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                Text(
                    text = correctionTitle,
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.titleLarge
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(125.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = correctionRequest?.reason ?: "",
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colorResource(id = R.color.gray_400))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Created at: ${formatDateToString(correctionRequest?.createdate) ?: ""}",
                        color = colorResource(id = R.color.gray_700),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.spaceSmall)
                    ) {
                        when {
                            correctionRequest?.approvedflag == true -> {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = colorResource(
                                        id = R.color.teal_A400
                                    ),
                                    modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                                )
                                Text(
                                    text = "Approved",
                                    color = colorResource(id = R.color.gray_700),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            correctionRequest?.rejectedflag == true -> {
                                Icon(
                                    imageVector = Icons.Filled.Block, contentDescription = null,
                                    tint = colorResource(
                                        id = R.color.red_800
                                    ),
                                    modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                                )
                                Text(
                                    text = "Rejected",
                                    color = colorResource(id = R.color.gray_700),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            correctionRequest?.approvedflag == false && correctionRequest.rejectedflag == false -> {
                                Icon(
                                    imageVector = Icons.Filled.Schedule, contentDescription = null,
                                    tint = colorResource(
                                        id = R.color.deep_orange_500
                                    ),
                                    modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                                )
                                Text(
                                    text = "Pending",
                                    color = colorResource(id = R.color.gray_700),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        HistoryCard(
//            "Correction",
//            "Forgot to tap in because of accident",
//            date = "Friday, 24 June 2023",
//            status = "Approved"
//        )
//    }
//}