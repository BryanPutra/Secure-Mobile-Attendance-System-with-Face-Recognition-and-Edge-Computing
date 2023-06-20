package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.getUserMonthlyToleranceWorkTime
import com.example.Thesis_Project.viewmodel.MainViewModel


@Composable
fun AdminViewUserDialog(mainViewModel: MainViewModel, user: User?, onCancelClicked: () -> Unit) {

    Dialog(
        onDismissRequest = { onCancelClicked() },
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
                    text = if (user?.adminflag == true) "Admin" else "User",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Name",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = user?.name ?: "",
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Email",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = user?.email ?: "",
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leave Allowed",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = if(user?.leaveallow == true) "Allowed" else "Not Allowed",
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leave Left",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = user?.leaveleft.toString(),
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tolerance Work Time",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = getUserMonthlyToleranceWorkTime(user?.monthlytoleranceworktime) ?: "",
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Left
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Close")
                    }
                }
            }
        }
    }
}

