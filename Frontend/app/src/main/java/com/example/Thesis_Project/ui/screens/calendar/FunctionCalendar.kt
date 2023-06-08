package com.example.Thesis_Project.ui.screens.calendar

import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.viewmodel.MainViewModel
import java.time.LocalDate

fun getAttendanceByDay(day: String, viewModel: MainViewModel): Attendance? {
    if (day.isEmpty()) {
        return null
    }
    var tempAttendance: Attendance? = null
    tempAttendance = viewModel.attendanceList?.find { attendance ->
        db_util.dateToLocalDate(attendance.timein!!).toString() == day
    }
    return tempAttendance
}

fun getAttendanceByDate(date: LocalDate, viewModel: MainViewModel): Attendance? {
    var tempAttendance: Attendance? = null
    tempAttendance = viewModel.attendanceList?.find { attendance ->
        db_util.dateToLocalDate(attendance.timein!!) == date
    }
    return tempAttendance
}