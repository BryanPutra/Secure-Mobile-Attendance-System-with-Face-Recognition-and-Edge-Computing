package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminUsersRow(user: User, mainViewModel: MainViewModel, onViewClick: (User) -> Unit) {

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
            Text(text = user.name ?: "No user found", color = colorResource(id = R.color.black), fontWeight = FontWeight.Bold)
            Text(
                text = formatDateToStringWithOrdinal(user.joindate) ?: "No user found",
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
                    onViewClick(user)
                },
                imageVector = Icons.Filled.Visibility,
                contentDescription = null,
                tint = colorResource(id = R.color.blue_500)
            )
            Icon(
                imageVector = Icons.Filled.FactCheck,
                contentDescription = null,
                tint = colorResource(id = R.color.teal_A400)
            )
        }
    }
}