package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import android.view.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.screens.calendar.getDurationFromDates
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.util.*
import kotlin.coroutines.suspendCoroutine

@Composable
fun LeaveRequestDialog(mainViewModel: MainViewModel) {

    val createLeaveRequestScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var dateFrom by rememberSaveable { mutableStateOf(mainViewModel.calendarSelectedDate) }
    var dateTo by rememberSaveable { mutableStateOf(mainViewModel.calendarSelectedDate.plusDays(1)) }
    var detail by rememberSaveable { mutableStateOf("") }
    var isPermission by rememberSaveable { mutableStateOf(false) }
    val disabledDatesState = rememberSaveable { mutableListOf<LocalDate>() }

    val calendarDateFromState = rememberUseCaseState()
    val calendarDateToState = rememberUseCaseState()

    var dateFromIsValid by remember { mutableStateOf(true) }
    var dateToIsValid by remember { mutableStateOf(true) }
    var dateIsValid by remember { mutableStateOf(false) }
    var confirmLeaveRequest by remember { mutableStateOf(false) }


    var errorText by remember { mutableStateOf("") }

    fun addDisabledDates() {
        val attendanceList = mainViewModel.attendanceList ?: return
        for (attendance in attendanceList) {
            if (attendance.leaveflag != true && attendance.permissionflag != true) {
                continue
            }
            attendance.timein?.let { disabledDatesState.add(db_util.dateToLocalDate(it)) }
        }
    }

    LaunchedEffect(Unit) {
        addDisabledDates()
    }

    val postCreateLeaveRequest: suspend (leaveRequest: LeaveRequest) -> Unit = { leaveRequest ->
        mainViewModel.setIsLoading(true)
        try {
            db_util.createLeaveRequest(mainViewModel.db, leaveRequest)
            db_util.getLeaveRequest(
                mainViewModel.db,
                mainViewModel.userData?.userid,
                mainViewModel.setLeaveRequestList
            )
            Log.d("getleaverequest after submit", "${mainViewModel.leaveRequestList}")
            errorText = ""
            mainViewModel.showToast(context, "Leave Request has been created successfully")
            mainViewModel.toggleRequestLeaveDialog()
            dateIsValid = false
            confirmLeaveRequest = false
        } catch (e: Exception) {
            errorText = "Failed to create leave request: ${e.message}"
            Log.e("Error", "Failed to create leave request: $e")
        }
        mainViewModel.setIsLoading(false)
    }

    val checkValidCreateLeaveRequest: suspend (leaveRequest: LeaveRequest) -> Unit =
        { leaveRequest ->
            mainViewModel.setIsLoading(true)
            try {
                db_util.checkValidLeaveRequestDate(
                    mainViewModel.db,
                    mainViewModel.userData?.userid!!,
                    leaveRequest.leavestart!!,
                    leaveRequest.duration!!,
                ) { isDateValid ->
                    dateIsValid = isDateValid == true
                }
                if (dateIsValid) {
                    db_util.checkPendingRequestDuration(
                        mainViewModel.db,
                        mainViewModel.userData?.userid!!,
                        leaveRequest.leavestart
                    ) { leaveAmount, permissionAmount ->
                        if (leaveAmount != null) {
                            if (leaveRequest.permissionflag!!) {
                                db_util.getTotalPermissionThisYear(
                                    mainViewModel.db,
                                    mainViewModel.userData?.userid!!
                                ) { data ->
                                    if (data != null) {
                                        if (leaveRequest.duration!! + data + permissionAmount!! > mainViewModel.companyVariable?.maxpermissionsleft!!) {
                                            confirmLeaveRequest = true
                                        } else {
                                            postCreateLeaveRequest(leaveRequest)
                                        }
                                    }
                                }
                            } else {
                                db_util.getTotalLeaveThisMonth(
                                    mainViewModel.db,
                                    mainViewModel.userData?.userid!!
                                ) { data ->
                                    if (data != null) {
                                        if (leaveRequest.duration!! + data + leaveAmount > mainViewModel.companyVariable?.maxmonthlyleaveleft!!) {
                                            mainViewModel.showToast(
                                                context,
                                                "Leave request exceeds monthly quota"
                                            )
                                            errorText = "Leave request exceeds monthly quota"
                                        } else if (leaveRequest.duration!! + leaveAmount > mainViewModel.userData?.leaveleft!!) {
                                            mainViewModel.showToast(
                                                context,
                                                "Not enough leave left to create request"
                                            )
                                            errorText = "Not enough leave left to create request"
                                        } else {
                                            postCreateLeaveRequest(leaveRequest)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    mainViewModel.showToast(
                        context,
                        "There is a pending request in the selected date"
                    )
                    errorText = "There is a pending request in the selected date"
                }
            } catch (e: Exception) {
                errorText = "Leave request is not valid: ${e.message}"
                Log.e("Error", "Failed to create leave request: $e")
            }
            dateIsValid = false
            mainViewModel.setIsLoading(false)
        }

    fun onConfirmCreateLeaveRequestClicked() {
        val leaveRequest = LeaveRequest(
            userid = mainViewModel.userData?.userid,
            leavestart = db_util.localDateToDate(dateFrom),
            leaveend = db_util.localDateToDate(dateTo),
            duration = getDurationFromDates(dateFrom, dateTo),
            permissionflag = isPermission,
            reason = detail,
        )
        createLeaveRequestScope.launch {
            postCreateLeaveRequest(leaveRequest)
        }
    }

    fun onRequestClicked() {
        dateFromIsValid = isValidLeaveRequestDateFrom(dateFrom)
        dateToIsValid = isValidLeaveRequestDateTo(dateFrom, dateTo)

        if (!dateFromIsValid) {
            errorText =
                "Date From cannot be earlier than today and can only request in the current month"
            return
        }

        if (!dateToIsValid) {
            errorText =
                "Date To cannot be earlier than Date From and can only request in the current month"
            return
        }

        val leaveRequest = LeaveRequest(
            userid = mainViewModel.userData?.userid,
            leavestart = db_util.localDateToDate(dateFrom),
            leaveend = db_util.localDateToDate(dateTo),
            duration = getDurationFromDates(dateFrom, dateTo),
            permissionflag = isPermission,
            reason = detail,
        )

        createLeaveRequestScope.launch {
            checkValidCreateLeaveRequest(leaveRequest)
        }
    }

    fun onCancelClicked() {
        mainViewModel.toggleRequestLeaveDialog()
    }

    if (confirmLeaveRequest) {
        AlertDialog(
            onDismissRequest = { confirmLeaveRequest = false },
            // Put popup permissions left not enough, can create but will deduct leave left
            // If leaveleft not enough will count as absent
            // If user agrees run createLeaveRequest
            title = { Text(text = "Confirm Leave Request") },
            text = { Text(text = "Not enough permissions left, you can still use your leave left to continue. If your leave left is not enough, the remaining dates will be counted as absent. Do you still want to continue?") },
            confirmButton = {
                Button(
                    onClick = {
                        confirmLeaveRequest = false
                        onConfirmCreateLeaveRequestClicked()
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { confirmLeaveRequest = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }


    CalendarDialog(
        state = calendarDateFromState,
        config = CalendarConfig(
            monthSelection = true,
            disabledDates = disabledDatesState
        ),
        selection = CalendarSelection.Date { newDateFrom -> dateFrom = newDateFrom }
    )
    CalendarDialog(
        state = calendarDateToState,
        config = CalendarConfig(
            monthSelection = true,
            disabledDates = disabledDatesState
        ),
        selection = CalendarSelection.Date { newDateTo -> dateTo = newDateTo }
    )

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
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    calendarDateFromState.show()
                                    addDisabledDates()
                                },
                            value = formatLocalDateToString(dateFrom),
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
                            text = "To",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    calendarDateToState.show()
                                    addDisabledDates()
                                },
                            value = formatLocalDateToString(dateTo),
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
                                onCheckedChange = { newIsPermission ->
                                    isPermission = newIsPermission
                                },
                                colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.blue_500))
                            )
                        }

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
//        LeaveRequestDialog()
//    }
//}