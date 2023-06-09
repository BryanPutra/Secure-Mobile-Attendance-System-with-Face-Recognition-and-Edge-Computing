package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.viewmodel.MainViewModel
import java.util.*

@Composable
fun LeaveRequestDialog(mainViewModel: MainViewModel? = null) {

    var dateFrom by rememberSaveable { mutableStateOf("28/03/2023") }
    var dateTo by rememberSaveable { mutableStateOf("31/03/2023") }
    var detail by rememberSaveable { mutableStateOf("I forgot my homework fuck this world") }
    var isPermission by rememberSaveable { mutableStateOf(false) }

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
            modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
        ) {
            Text(
                text = "Leave Request",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
            )
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "From",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = dateFrom,
                        onValueChange = { newDateFrom -> dateFrom = newDateFrom },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = colorResource(id = R.color.blue_500)
                            )
                        })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "To",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = dateTo,
                        onValueChange = { newDateTo -> dateTo = newDateTo },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = colorResource(id = R.color.blue_500)
                            )
                        })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Detail",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(2f)
                            .height(120.dp),
                        value = detail,
                        onValueChange = { newDetail -> detail = newDetail })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Permission",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    Box(modifier = Modifier.weight(2f)) {
                        Checkbox(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = -(16.dp))
                                .padding(0.dp),
                            checked = isPermission,
                            onCheckedChange = { newIsPermission -> isPermission = newIsPermission },
                            colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.blue_500))
                        )
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClickCallback = { /*TODO*/ }, buttonText = "Request")
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClickCallback = { /*TODO*/ }, buttonText = "Cancel")
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
//        LeaveRequestDialog()
//    }
//}