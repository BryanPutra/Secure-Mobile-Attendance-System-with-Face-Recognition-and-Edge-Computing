package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringTimeOnly
import com.example.Thesis_Project.ui.utils.formatLocalDateToString
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.util.*

@Composable
fun CorrectionRequestDialog(mainViewModel: MainViewModel, selectedAttendance: Attendance? = null) {

    var date by rememberSaveable { mutableStateOf(mainViewModel.calendarSelectedDate) }
    var tapInTime: String? by rememberSaveable {
        mutableStateOf(
            formatDateToStringTimeOnly(
                selectedAttendance?.timein
            )
        )
    }
    var tapOutTime: String? by rememberSaveable {
        mutableStateOf(
            formatDateToStringTimeOnly(
                selectedAttendance?.timeout
            )
        )
    }
    var detail by rememberSaveable { mutableStateOf("I forgot my homework fuck this world") }
    var presentFlag by rememberSaveable { mutableStateOf(selectedAttendance?.absentflag == false && selectedAttendance.leaveflag == false && selectedAttendance.permissionflag == false) }
    var leaveFlag by rememberSaveable { mutableStateOf(selectedAttendance?.leaveflag == true) }
    var permissionFlag by rememberSaveable { mutableStateOf(selectedAttendance?.permissionflag == true) }

    val calendarDateState = rememberUseCaseState()
    val clockTapInTimeState = rememberUseCaseState()
    val clockTapOutTimeState = rememberUseCaseState()

    fun onRequestClicked() {
        mainViewModel.isCorrectionLeaveDialogShown = false
    }

    fun onCancelClicked() {
        mainViewModel.isCorrectionLeaveDialogShown = false
    }

    CalendarDialog(
        state = calendarDateState,
        config = CalendarConfig(
            monthSelection = true,
        ),
        selection = CalendarSelection.Date { newDate -> date = newDate }
    )

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
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    calendarDateState.show()
                                },
                            value = formatLocalDateToString(date),
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
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorResource(
                                                id = R.color.blue_500
                                            )
                                        )
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
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorResource(
                                                id = R.color.blue_500
                                            )
                                        )
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
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorResource(
                                                id = R.color.blue_500
                                            )
                                        )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = MaterialTheme.spacing.spaceMedium),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(
                                onClick = { onRequestClicked() },
                                buttonText = "Request"
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
//        CorrectionRequestDialog()
//    }
//}