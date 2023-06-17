package com.example.Thesis_Project.ui.components

import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.formatLocalDateToString
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import java.util.*

@Composable
fun AdminEditCompanyParamsDialog(mainViewModel: MainViewModel) {

    var leaveleft by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.leaveleft.toString()) }
    var maxTotalLeaveLeft by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.maxtotalleaveleft.toString()) }
    var minimumDaysWorked by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.minimumdaysworked.toString()) }
    var maxMonthlyLeaveLeft by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.maxmonthlyleaveleft.toString()) }
    var wifiSSID by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.wifissid) }
    var tapInTime by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.tapintime) }
    var tapOutTime by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.tapouttime) }
    var companyWorkTime by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.companyworktime.toString()) }
    var toleranceWorkTime by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.toleranceworktime.toString()) }
    var maxCompensateTime by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.maxcompensatetime.toString()) }
    var maxPermissionLeft by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.maxpermissionsleft.toString()) }

    val clockTapInTimeState = rememberUseCaseState()
    val clockTapOutTimeState = rememberUseCaseState()

    fun onSubmitClicked() {
        mainViewModel.isEditCompanyParamsDialogShown = false
    }

    fun onCancelClicked() {
        mainViewModel.isEditCompanyParamsDialogShown = false
    }

    ClockDialog(
        state = clockTapInTimeState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapInTime = "$hours:$minutes"
        })
    ClockDialog(
        state = clockTapOutTimeState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapOutTime = "$hours:$minutes"
        })

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
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
                    text = "Edit Company Params",
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
                            text = "Tap In",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    clockTapInTimeState.show()
                                },
                            value = tapInTime ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = colorResource(id = R.color.black),
                                disabledTextColor = colorResource(id = R.color.black),
                                disabledLabelColor = colorResource(id = R.color.black)
                            ),
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
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    clockTapOutTimeState.show()
                                },
                            value = tapOutTime ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = colorResource(id = R.color.black),
                                disabledTextColor = colorResource(id = R.color.black),
                                disabledLabelColor = colorResource(id = R.color.black)
                            ),
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
                            text = "Max Leave",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxTotalLeaveLeft,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxTotalLeaveLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leave per Year",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = leaveleft,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                leaveleft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leave per Month",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxMonthlyLeaveLeft,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxMonthlyLeaveLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Min Days",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = minimumDaysWorked,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                minimumDaysWorked = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Permission Per Year",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxPermissionLeft,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxPermissionLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
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
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = toleranceWorkTime,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                toleranceWorkTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Company Work Time",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = companyWorkTime,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                companyWorkTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Compensate Work Time",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxCompensateTime,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxCompensateTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(text = "Days", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wifi SSID",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = wifiSSID ?: "",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                wifiSSID = text
                            },
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(
                                onClick = { onSubmitClicked() },
                                buttonText = "Submit"
                            )
                        }
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Cancel")
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
//        LeaveRequestDialog()
//    }
//}