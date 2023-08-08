package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun AdminUsersApproveCorrectionRow(
    mainViewModel: MainViewModel,
    correctionRequest: CorrectionRequest,
    onViewClick: (CorrectionRequest) -> Unit,
    onApproveClick: (CorrectionRequest) -> Unit,
    onRejectClick: (CorrectionRequest) -> Unit
) {

    var selectedCorrectionRequestAttendance: Attendance? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        runBlocking {
            mainViewModel.setIsLoading(true)
            db_util.getAttendanceById(
                mainViewModel.db,
                correctionRequest.attendanceid!!
            ) { newCorrectionRequestAttendance ->
                selectedCorrectionRequestAttendance = newCorrectionRequestAttendance
            }
            mainViewModel.setIsLoading(false)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.spaceMedium)
            .clickable {
                onViewClick(correctionRequest)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceSmall)
        ) {
            if (selectedCorrectionRequestAttendance != null) {
                Text(
                    text = if (correctionRequest.leaveflag == true || correctionRequest.permissionflag == true || correctionRequest.presentflag == true) {
                        when {
                            correctionRequest.leaveflag == true -> "Absent to Leave"
                            correctionRequest.permissionflag == true -> "Absent to Permission"
                            else -> "Absent to Present"
                        }
                    } else {when {
                        selectedCorrectionRequestAttendance?.permissionflag == true -> "Change permission date"
                        selectedCorrectionRequestAttendance?.leaveflag == true -> "Change leave date"
                        else -> "Change attendance time"
                    }},
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Created at: ${formatDateToStringWithOrdinal(correctionRequest.createdate)}" ,
                color = colorResource(id = R.color.gray_400),
                fontSize = 12.sp
            )
        }
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.clickable {
                    onApproveClick(correctionRequest)
                },
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = colorResource(id = R.color.teal_A400)
            )
            Icon(
                modifier = Modifier.clickable {
                    onRejectClick(correctionRequest)
                },
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = colorResource(id = R.color.red_800)
            )
        }
    }
}