package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@Composable
fun CorrectionRequestDialog(mainViewModel: MainViewModel? = null) {
    var date by rememberSaveable { mutableStateOf("28/03/2023") }
    var tapInTime by rememberSaveable { mutableStateOf("08:30") }
    var tapOutTime by rememberSaveable { mutableStateOf("17:30") }
    var detail by rememberSaveable { mutableStateOf("I forgot my homework fuck this world") }
    var presentFlag by rememberSaveable { mutableStateOf(false) }
    var leaveFlag by rememberSaveable { mutableStateOf(false) }
    var permissionFlag by rememberSaveable { mutableStateOf(false) }



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
                text = "Correction Request",
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
                        text = "Date",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = date,
                        onValueChange = { newDate -> date = newDate },
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
                        text = "Tap In",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = tapInTime,
                        onValueChange = { newTapInTime -> tapInTime = newTapInTime },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
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
                        text = "Tap Out",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = tapOutTime,
                        onValueChange = { newTapOutTime -> tapOutTime = newTapOutTime },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
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
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Status",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box() {
                                Checkbox(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(x = -(16.dp), y = -(12.dp))
                                        .padding(0.dp),
                                    checked = presentFlag,
                                    onCheckedChange = { newPresentFlag ->
                                        presentFlag = newPresentFlag
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.blue_500))
                                )
                            }
                            Text(
                                text = "Present",
                                modifier = Modifier,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box() {
                                Checkbox(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(x = -(16.dp), y = -(12.dp))
                                        .padding(0.dp),
                                    checked = leaveFlag,
                                    onCheckedChange = { newLeaveFlag ->
                                        leaveFlag = newLeaveFlag
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.blue_500))
                                )
                            }
                            Text(
                                text = "Leave",
                                modifier = Modifier,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box() {
                                Checkbox(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(x = -(16.dp), y = -(12.dp))
                                        .padding(0.dp),
                                    checked = permissionFlag,
                                    onCheckedChange = { newPermissionFlag ->
                                        permissionFlag = newPermissionFlag
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.blue_500))
                                )
                            }
                            Text(
                                text = "Permission",
                                modifier = Modifier,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left
                            )
                        }
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
//        CorrectionRequestDialog()
//    }
//}