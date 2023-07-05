package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.CompanyParams
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import kotlinx.coroutines.launch

@Composable
fun AdminEditCompanyParamsDialog(mainViewModel: MainViewModel) {

    val updateCompanyParamsScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var leaveLeft by rememberSaveable { mutableStateOf(mainViewModel.companyVariable?.leaveleft.toString()) }
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

    var tapInIsValid by remember { mutableStateOf(true) }
    var tapOutIsValid by remember { mutableStateOf(true) }
    var maxLeaveIsValid by remember { mutableStateOf(true) }
    var leavePerYearIsValid by remember { mutableStateOf(true) }
    var leavePerMonthIsValid by remember { mutableStateOf(true) }
    var minimumDaysWorkedIsValid by remember { mutableStateOf(true) }
    var permissionPerYearIsValid by remember { mutableStateOf(true) }
    var toleranceWorkTimeIsValid by remember { mutableStateOf(true) }
    var companyWorkTimeIsValid by remember { mutableStateOf(true) }
    var maxCompensateWorkTimeIsValid by remember { mutableStateOf(true) }
    var wifiSSIDIsValid by remember { mutableStateOf(true) }

    var errorText by remember { mutableStateOf("") }

    val clockTapInTimeState = rememberUseCaseState()
    val clockTapOutTimeState = rememberUseCaseState()

    val postEditCompanyParams: suspend (companyParams: CompanyParams) -> Unit = { companyParams ->
        mainViewModel.setIsLoading(true)
        mainViewModel.companyVariable?.let {
            try {
                db_util.updateCompanyParams(
                    mainViewModel.db,
                    companyParams
                )
                db_util.getCompanyParams(mainViewModel.db, mainViewModel.setCompanyVariable)
                errorText = ""
                mainViewModel.showToast(context, "Company Params updated successfully")
                mainViewModel.toggleIsEditCompanyParamsDialogShown()
            } catch (e: Exception) {
                errorText = "Failed to update company params: ${e.message}"
                Log.e("Error", "Failed to update company params: $e")
            }
        }
        mainViewModel.setIsLoading(false)
    }

    fun onSubmitClicked() {

        tapInIsValid = isValidTapIn(tapInTime)
        tapOutIsValid = isValidTapOut(tapOutTime)
        maxLeaveIsValid = isValidMaxLeave(maxTotalLeaveLeft)
        leavePerYearIsValid = isValidLeavePerYear(leaveLeft)
        leavePerMonthIsValid = isValidLeavePerMonth(maxMonthlyLeaveLeft)
        minimumDaysWorkedIsValid = isValidMinimumDaysWorked(minimumDaysWorked)
        permissionPerYearIsValid = isValidPermissionPerYear(maxPermissionLeft)
        toleranceWorkTimeIsValid = isValidToleranceWorkTime(toleranceWorkTime)
        companyWorkTimeIsValid = isValidCompanyWorkTime(companyWorkTime)
        maxCompensateWorkTimeIsValid = isValidMaxCompensateWorkTime(maxCompensateTime)
        wifiSSIDIsValid = isValidWifiSSID(wifiSSID)

        if (!tapInIsValid) {
            errorText = "Please fill tap in"
            return
        }

        if (!tapOutIsValid) {
            errorText = "Please fill tap out"
            return
        }

        if (!maxLeaveIsValid) {
            errorText = "Please fill max leave"
            return
        }
        if (!leavePerYearIsValid) {
            errorText = "Please fill leave per year"
            return
        }
        if (!leavePerMonthIsValid) {
            errorText = "Please fill leave per month"
            return
        }
        if (!minimumDaysWorkedIsValid) {
            errorText = "Please fill minimum days worked"
            return
        }
        if (!permissionPerYearIsValid) {
            errorText = "Please fill permission per year"
            return
        }
        if (!toleranceWorkTimeIsValid) {
            errorText = "Please fill tolerance work time"
            return
        }
        if (!companyWorkTimeIsValid) {
            errorText = "Please fill company work time"
            return
        }
        if (!maxCompensateWorkTimeIsValid) {
            errorText = "Please fill max compensation work time"
            return
        }
        if (!wifiSSIDIsValid) {
            errorText = "Please fill wifi SSID"
            return
        }
        val companyParams = CompanyParams(
            leaveleft = leaveLeft.toInt(),
            maxtotalleaveleft = maxTotalLeaveLeft.toInt(),
            minimumdaysworked = minimumDaysWorked.toInt(),
            maxmonthlyleaveleft = maxMonthlyLeaveLeft.toInt(),
            wifissid = wifiSSID,
            tapintime = tapInTime,
            tapouttime = tapOutTime,
            companyworktime = companyWorkTime.toInt(),
            toleranceworktime = toleranceWorkTime.toInt(),
            maxcompensatetime = maxCompensateTime.toInt(),
            maxpermissionsleft = maxPermissionLeft.toInt(),
            )
        updateCompanyParamsScope.launch {
            postEditCompanyParams(companyParams)
        }
    }

    fun onCancelClicked() {
        mainViewModel.toggleIsEditCompanyParamsDialogShown()
    }

    ClockDialog(
        state = clockTapInTimeState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapInTime = "${if (hours < 10) "0" else ""}$hours:${if (minutes < 10) "0" else ""}$minutes"
        })
    ClockDialog(
        state = clockTapOutTimeState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapOutTime = "${if (hours < 10) "0" else ""}$hours:${if (minutes < 10) "0" else ""}$minutes"
        })

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
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
                    textAlign = TextAlign.Center,
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
                            style = MaterialTheme.typography.titleMedium,
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
                            isError = !tapInIsValid,
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
                            style = MaterialTheme.typography.titleMedium,
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
                            isError = !tapOutIsValid,
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxTotalLeaveLeft,
                            isError = !maxLeaveIsValid,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxTotalLeaveLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium),text = "Days", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = leaveLeft,
                            isError = !leavePerYearIsValid,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                leaveLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Days", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxMonthlyLeaveLeft,
                            isError = !leavePerMonthIsValid,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxMonthlyLeaveLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Days", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = minimumDaysWorked,
                            isError = !minimumDaysWorkedIsValid,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                minimumDaysWorked = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Days", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxPermissionLeft,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = !permissionPerYearIsValid,
                            onValueChange = { text ->
                                maxPermissionLeft = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Days", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = toleranceWorkTime,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = !toleranceWorkTimeIsValid,
                            onValueChange = { text ->
                                toleranceWorkTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Minutes", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = companyWorkTime,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = !companyWorkTimeIsValid,
                            onValueChange = { text ->
                                companyWorkTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Minutes", color = colorResource(id = R.color.blue_500))
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Max Compensation Work Time",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = maxCompensateTime,
                            isError = !maxCompensateWorkTimeIsValid,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { text ->
                                maxCompensateTime = text.replace(Regex("[^0-9]"), "")
                            },
                            trailingIcon = {
                                Text(modifier = Modifier.padding(end = MaterialTheme.spacing.spaceMedium), text = "Minutes", color = colorResource(id = R.color.blue_500))
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = wifiSSID ?: "",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = !wifiSSIDIsValid,
                            onValueChange = { text ->
                                wifiSSID = text
                            },
                        )
                    }
                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Cancel")
                        }
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(
                                onClick = { onSubmitClicked() },
                                buttonText = "Submit"
                            )
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