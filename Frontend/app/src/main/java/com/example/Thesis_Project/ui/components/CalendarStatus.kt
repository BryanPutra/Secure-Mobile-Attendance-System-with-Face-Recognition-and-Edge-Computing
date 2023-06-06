package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.CalendarStatusItem

@Composable
fun CalendarStatus(statusItem: CalendarStatusItem){
    val statusColor = colorResource(id = statusItem.statusColor)

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceSmall)
    ) {
        Canvas(modifier = Modifier.size(12.dp), ){
            drawCircle(color = statusColor)
        }
        Text(text = statusItem.statusName, style = MaterialTheme.typography.labelMedium)
    }
}