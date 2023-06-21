package com.example.Thesis_Project.ui.screens.calendar

import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.ui.component_item_model.DayOfMonthItem
import com.example.Thesis_Project.ui.utils.isAttended
import com.example.Thesis_Project.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun getAttendanceByDay(day: String, mainViewModel: MainViewModel): Attendance? {
    if (day.isEmpty()) {
        return null
    }
    val tempAttendance: Attendance? = mainViewModel.attendanceList?.find { attendance ->
        db_util.dateToLocalDate(attendance.timein!!).dayOfMonth.toString() == day
    }
    return tempAttendance
}

fun getAttendanceByDate(date: LocalDate, mainViewModel: MainViewModel): Attendance? {
    var tempAttendance: Attendance? = null
    tempAttendance = mainViewModel.attendanceList?.find { attendance ->
        db_util.dateToLocalDate(attendance.timein!!) == date
    }
    return tempAttendance
}

fun checkIfAttendanceOnCorrectionPending(
    attendance: Attendance?,
    mainViewModel: MainViewModel
): Boolean {
    var tempCorrectionRequest: CorrectionRequest? = null
    if (attendance != null) {
        tempCorrectionRequest = mainViewModel.correctionRequestList?.find { correctionRequest ->
            correctionRequest.attendanceid == attendance.attendanceid && (correctionRequest.approvedflag == false || correctionRequest.rejectedflag == false)
        }
    }
    return tempCorrectionRequest != null
}

fun getDurationFromDates(dateFrom: LocalDate, dateTo: LocalDate): Int {
    return ChronoUnit.DAYS.between(dateFrom, dateTo).toInt() + 1
}

fun checkIsSelected(dayOfMonth: DayOfMonthItem, mainViewModel: MainViewModel): Boolean {
    return mainViewModel.calendarSelectedDate == dayOfMonth.date
}

fun checkIsAttended(dayOfMonth: DayOfMonthItem, mainViewModel: MainViewModel): Boolean {
    if (dayOfMonth.date == null) {
        return false
    }
    val tempAttendance: Attendance? = getAttendanceByDate(dayOfMonth.date, mainViewModel)
    if (tempAttendance != null) {
        return isAttended(tempAttendance)
    }
    return false
}

fun checkIsAbsent(dayOfMonth: DayOfMonthItem, mainViewModel: MainViewModel): Boolean {
    if (dayOfMonth.date == null) {
        return false
    }
    val tempAttendance: Attendance? = getAttendanceByDate(dayOfMonth.date, mainViewModel)
    if (tempAttendance != null) {
        return tempAttendance.absentflag == true
    }
    return false
}

fun checkIsLeaveOrPermission(dayOfMonth: DayOfMonthItem, mainViewModel: MainViewModel): Boolean {
    if (dayOfMonth.date == null) {
        return false
    }
    val tempAttendance: Attendance? = getAttendanceByDate(dayOfMonth.date, mainViewModel)
    if (tempAttendance != null) {
        return tempAttendance.leaveflag == true || tempAttendance.permissionflag == true
    }
    return false
}

fun setDateTextColor(
    isSelected: Boolean,
    isAttended: Boolean,
    isAbsent: Boolean,
    isLeave: Boolean
): Int {
    return when {
        isSelected -> R.color.white
        isAbsent -> R.color.red_800
        isLeave -> R.color.light_orange_300
        isAttended -> R.color.teal_600
        else -> R.color.black
    }
}