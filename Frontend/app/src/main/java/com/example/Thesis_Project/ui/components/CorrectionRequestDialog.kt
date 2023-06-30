package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CorrectionRequestDialog(mainViewModel: MainViewModel, selectedAttendance: Attendance? = null) {
    val createCorrectionRequestScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    val getLeavePermission =
        if (selectedAttendance?.leaveflag == true || selectedAttendance?.permissionflag == true) {
            if (selectedAttendance.leaveflag == true) {
                mainViewModel.leaveRequestList?.find {
                    it.permissionflag == false && it.approvedflag == true && (checkHaveSameDates(
                        it.leavestart,
                        selectedAttendance.timein
                    ) || checkHaveSameDates(it.leaveend, selectedAttendance.timein))
                }
            } else {
                mainViewModel.leaveRequestList?.find {
                    it.permissionflag == true && it.approvedflag == true && (checkHaveSameDates(
                        it.leavestart,
                        selectedAttendance.timein
                    ) || checkHaveSameDates(it.leaveend, selectedAttendance.timein))
                }
            }
        } else {
            null
        }
    val leavePermissionAttendance by mutableStateOf(getLeavePermission)
    val date by rememberSaveable { mutableStateOf(mainViewModel.calendarSelectedDate) }
    var dateLeavePermission: LocalDate by rememberSaveable { mutableStateOf(mainViewModel.calendarSelectedDate) }

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
    var detail by rememberSaveable { mutableStateOf("") }
    var presentFlag by rememberSaveable { mutableStateOf(isAttended(selectedAttendance)) }
    var leaveFlag by rememberSaveable { mutableStateOf(selectedAttendance?.leaveflag == true) }
    var permissionFlag by rememberSaveable { mutableStateOf(selectedAttendance?.permissionflag == true) }

    val disabledDatesState = rememberSaveable { mutableListOf<LocalDate>() }

    val calendarDateLeavePermissionState = rememberUseCaseState()
    val clockTapInTimeState = rememberUseCaseState()
    val clockTapOutTimeState = rememberUseCaseState()

    var dateLeavePermissionIsValid by remember { mutableStateOf(true) }
    var statusIsValid by remember { mutableStateOf(true) }
    var detailIsValid by remember { mutableStateOf(true) }
    var timeInPresentIsValid by remember { mutableStateOf(true) }
    var timeOutIsValid by remember { mutableStateOf(true) }

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

    val postCreateCorrectionRequest: suspend (correctionRequest: CorrectionRequest) -> Unit =
        { correctionRequest ->
            mainViewModel.setIsLoading(true)
            try {
                db_util.createCorrectionRequest(mainViewModel.db, correctionRequest)
                db_util.getCorrectionRequest(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    mainViewModel.setCorrectionRequestList
                )
                errorText = ""
                mainViewModel.showToast(context, "Correction Request has been created successfully")
                mainViewModel.toggleCorrectionDialog()
            } catch (e: Exception) {
                errorText = "Failed to create correction request: ${e.message}"
                Log.e("Error", "Failed to create correction request: $e")
            }
            mainViewModel.setIsLoading(false)
        }

    val checkValidCreateCorrectionRequest: suspend (correctionRequest: CorrectionRequest) -> Unit =
        { correctionRequest ->
            mainViewModel.setIsLoading(true)
            Log.d("RUNNING CHECKCORRECTIONREQUEST", "$correctionRequest")
            try {
                db_util.checkCorrectionRequestExist(
                    mainViewModel.db,
                    correctionRequest.attendanceid!!
                ) { exist ->
                    if (exist != null) {
                        if (!exist) {
                            db_util.checkPendingRequestDuration(
                                mainViewModel.db,
                                mainViewModel.userData?.userid!!,
                                correctionRequest.timein!!
                            ) { leaveamt, permamt ->
                                if (leaveamt != null) {
                                    if (correctionRequest.permissionflag == true) {
                                        db_util.getTotalPermissionThisYear(
                                            mainViewModel.db,
                                            mainViewModel.userData?.userid!!,
                                        ) { data ->
                                            if (data != null) {
                                                if (1 + data + permamt!! > mainViewModel.companyVariable?.maxpermissionsleft!!) {
                                                    mainViewModel.showToast(
                                                        context,
                                                        "Not enough permissions left to create request"
                                                    )
                                                    errorText =
                                                        "Not enough permissions left to create request"
                                                } else {
                                                    postCreateCorrectionRequest(correctionRequest)
                                                }
                                            }
                                        }
                                    } else if (correctionRequest.leaveflag == true) {
                                        if (mainViewModel.userData?.leaveallow!!) {
                                            db_util.getTotalLeaveThisMonth(
                                                mainViewModel.db,
                                                mainViewModel.userData?.userid!!,
                                            ) { data ->
                                                if (data != null) {
                                                    if (1 + data + leaveamt > mainViewModel.companyVariable?.maxmonthlyleaveleft!!) {
                                                        mainViewModel.showToast(
                                                            context,
                                                            "Leave request exceeds monthly quota"
                                                        )
                                                        errorText =
                                                            "Leave request exceeds monthly quota"
                                                    } else if (1 + leaveamt > mainViewModel.userData?.leaveleft!!) {
                                                        mainViewModel.showToast(
                                                            context,
                                                            "Not enough leave left to create request"
                                                        )
                                                        errorText =
                                                            "Not enough leave left to create request"
                                                    } else {
                                                        postCreateCorrectionRequest(
                                                            correctionRequest
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            mainViewModel.showToast(
                                                context,
                                                "Leave not allowed for current user"
                                            )
                                            errorText = "Leave not allowed for current user"
                                        }
                                    } else if (correctionRequest.presentflag == true) {
                                        postCreateCorrectionRequest(correctionRequest)
                                    } else {
                                        if (selectedAttendance?.permissionflag!! || selectedAttendance?.leaveflag!!) {
                                            db_util.checkValidCorrectionRequestDate(
                                                mainViewModel.db,
                                                mainViewModel.userData?.userid!!,
                                                correctionRequest.timein
                                            ) { valid ->
                                                if (valid != null) {
                                                    if (valid == false) {
                                                        mainViewModel.showToast(
                                                            context,
                                                            "New selected date overlaps with an existing attendance/request"
                                                        )
                                                        errorText =
                                                            "New selected date overlaps with an existing attendance/request"
                                                    } else {
                                                        postCreateCorrectionRequest(
                                                            correctionRequest
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            postCreateCorrectionRequest(correctionRequest)
                                        }
                                    }
                                }
                            }
                        } else {
                            mainViewModel.showToast(
                                context,
                                "A request is found on the selected date, please choose an another date"
                            )
                            errorText = "A request is found on the selected date, please choose an another date"
                        }
                    }
                }
            } catch (e: Exception) {
                errorText = "Correction request is not valid: ${e.message}"
                Log.e("Error", "Failed to create correction request: $e")
            }
            mainViewModel.setIsLoading(false)
        }


    fun onRequestClicked() {

        var tempCorrectionRequest: CorrectionRequest? = null

        detailIsValid = isValidDetailRequest(detail)

        if (!detailIsValid) {
            errorText = "Please fill in the details"
            return
        }
        if (selectedAttendance?.absentflag == true) {
            statusIsValid = leaveFlag == true || permissionFlag == true || presentFlag == true
            if (!statusIsValid) {
                errorText = "Please check one of the status"
                return
            }
            if (leaveFlag) {
                tempCorrectionRequest = CorrectionRequest(
                    userid = mainViewModel.userData?.userid,
                    reason = detail,
                    leaveflag = leaveFlag,
                    attendanceid = selectedAttendance.attendanceid,
                    timein = db_util.companyTimeIn(
                        db_util.localDateToLocalDateTime(dateLeavePermission),
                        mainViewModel.companyVariable!!
                    ),
                    timeout = db_util.companyTimeOut(
                        db_util.localDateToLocalDateTime(dateLeavePermission),
                        mainViewModel.companyVariable!!
                    )
                )
            }

            if (permissionFlag) {
                tempCorrectionRequest = CorrectionRequest(
                    userid = mainViewModel.userData?.userid,
                    reason = detail,
                    permissionflag = permissionFlag,
                    attendanceid = selectedAttendance.attendanceid,
                    timein = db_util.companyTimeIn(
                        db_util.localDateToLocalDateTime(dateLeavePermission),
                        mainViewModel.companyVariable!!
                    ),
                    timeout = db_util.companyTimeOut(
                        db_util.localDateToLocalDateTime(dateLeavePermission),
                        mainViewModel.companyVariable!!
                    )
                )
            }

            if (presentFlag) {
                timeInPresentIsValid = isValidPresentTimeIn(
                    tapInTime,
                    mainViewModel.companyVariable?.tapintime,
                    mainViewModel.companyVariable?.tapouttime
                )
                timeOutIsValid = isValidTimeOut(tapInTime, tapOutTime)
                if (!timeOutIsValid) {
                    errorText = "Tap out time has to be later than Tap in time"
                    return
                }
                tempCorrectionRequest = CorrectionRequest(
                    userid = mainViewModel.userData?.userid,
                    reason = detail,
                    presentflag = presentFlag,
                    attendanceid = selectedAttendance.attendanceid,
                    timein = replaceTimeInDate(selectedAttendance.timein, tapInTime),
                    timeout = replaceTimeInDate(selectedAttendance.timein, tapOutTime)
                )
            }
        }

        if (selectedAttendance?.leaveflag == true || selectedAttendance?.permissionflag == true) {
            dateLeavePermissionIsValid = isValidLeaveRequestDateLeavePermission(dateLeavePermission)
            if (!dateLeavePermissionIsValid) {
                errorText =
                    "Date needs to be in the current month"
                return
            }
            tempCorrectionRequest = CorrectionRequest(
                userid = mainViewModel.userData?.userid,
                reason = detail,
                attendanceid = selectedAttendance.attendanceid,
                timein = db_util.companyTimeIn(
                    db_util.localDateToLocalDateTime(dateLeavePermission),
                    mainViewModel.companyVariable!!
                ),
                timeout = db_util.companyTimeOut(
                    db_util.localDateToLocalDateTime(dateLeavePermission),
                    mainViewModel.companyVariable!!
                )
            )
        }

        if (isAttended(selectedAttendance)) {
            timeOutIsValid = isValidTimeOut(tapInTime, tapOutTime)
            if (!timeOutIsValid) {
                errorText = "Tap out time has to be later than Tap in time"
                return
            }
            tempCorrectionRequest = CorrectionRequest(
                userid = mainViewModel.userData?.userid,
                reason = detail,
                attendanceid = selectedAttendance?.attendanceid,
                timein = replaceTimeInDate(selectedAttendance?.timein, tapInTime),
                timeout = replaceTimeInDate(selectedAttendance?.timein, tapOutTime)
            )
            Log.d("tempCorrectionRequest", "$tempCorrectionRequest")
        }

        createCorrectionRequestScope.launch {
            if (tempCorrectionRequest != null) {
                checkValidCreateCorrectionRequest(tempCorrectionRequest)
            }
        }
    }

    fun onCancelClicked() {
        mainViewModel.toggleCorrectionDialog()
    }

    CalendarDialog(
        state = calendarDateLeavePermissionState,
        config = CalendarConfig(
            monthSelection = true,
            disabledDates = disabledDatesState
        ),
        selection = CalendarSelection.Date { newDateLeavePermission ->
            dateLeavePermission = newDateLeavePermission
        }
    )

    ClockDialog(
        state = clockTapInTimeState,
        config = ClockConfig(
            is24HourFormat = true
        ),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapInTime =
                "${if (hours < 10) "0" else ""}$hours:${if (minutes < 10) "0" else ""}$minutes"
        })
    ClockDialog(
        state = clockTapOutTimeState,
        config = ClockConfig(
            is24HourFormat = true
        ),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            tapOutTime =
                "${if (hours < 10) "0" else ""}$hours:${if (minutes < 10) "0" else ""}$minutes"
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
                    text = "Correction Request",
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
                            text = "Correction",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = when {
                                selectedAttendance?.leaveflag == true -> "Change leave date"
                                selectedAttendance?.permissionflag == true -> "Change permission date"
                                isAttended(selectedAttendance) -> "Change attendance time"
                                selectedAttendance?.absentflag == true -> "Absent to Permission/Leave/Present"
                                else -> ""
                            },
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (!(selectedAttendance?.leaveflag == true || selectedAttendance?.permissionflag == true)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Date",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Right
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(2f),
                                value = formatLocalDateToString(date),
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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
                    }
                    if (selectedAttendance?.leaveflag == true || selectedAttendance?.permissionflag == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Date",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Right
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(2f)
                                    .clickable {
                                        calendarDateLeavePermissionState.show()
                                    },
                                value =
                                if (leavePermissionAttendance != null) {
                                    formatLocalDateToString(
                                        dateLeavePermission
                                    )
                                } else "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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
                    }
                    if (selectedAttendance?.let { isAttended(it) } == true || selectedAttendance?.absentflag == true) {
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
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                isError = !timeOutIsValid,
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
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Detail",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f)
                                .height(120.dp),
                            value = detail,
                            isError = !detailIsValid,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            onValueChange = { newDetail -> detail = newDetail })
                    }
                    if (selectedAttendance?.absentflag == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Status",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
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
                                                leaveFlag = false
                                                permissionFlag = false
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
                                        style = MaterialTheme.typography.titleMedium,
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
                                                permissionFlag = false
                                                presentFlag = false
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
                                        style = MaterialTheme.typography.titleMedium,
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
                                                leaveFlag = false
                                                presentFlag = false
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
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            }
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