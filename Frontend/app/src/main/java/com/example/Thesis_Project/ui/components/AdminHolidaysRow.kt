package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Holiday
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminHolidaysRow(
    holiday: Holiday,
    onDeleteClick: (Holiday) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatDateToStringForInputs(holiday.date) ?: "date not found",
            color = colorResource(id = R.color.black),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.clickable {
                    onDeleteClick(holiday)
                },
                imageVector = Icons.Filled.DeleteForever,
                contentDescription = null,
                tint = colorResource(id = R.color.red_800)
            )
        }
    }
}