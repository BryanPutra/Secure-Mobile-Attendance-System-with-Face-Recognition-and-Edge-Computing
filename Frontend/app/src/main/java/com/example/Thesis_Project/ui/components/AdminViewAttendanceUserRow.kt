package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminViewAttendanceRow(
    mainViewModel: MainViewModel,
    attendance: Attendance,
    onViewClick: (Attendance) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceSmall)
        ) {
            Text(
                text = "${formatDateToStringWithOrdinal(attendance.timein)}",
                color = colorResource(id = R.color.black),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Status: ${
                    when {
                        attendance.leaveflag == true -> "Leave"
                        attendance.permissionflag == true -> "Permission"
                        attendance.absentflag == true -> "Absent"
                        else -> "Present"
                    }
                }",
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
                    onViewClick(attendance)
                },
                imageVector = Icons.Filled.Visibility,
                contentDescription = null,
                tint = colorResource(id = R.color.blue_500)
            )
        }
    }
}