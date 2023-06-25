package com.example.Thesis_Project.ui.utils

import android.util.Patterns
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import java.time.LocalDate

//edit company variables
fun isValidTapIn(tapInTime: String?): Boolean {
    if (tapInTime != null) {
        return tapInTime.isNotEmpty()
    }
    return false
}

fun isValidTapOut(tapOutTime: String?): Boolean {
    if (tapOutTime != null) {
        return tapOutTime.isNotEmpty()
    }
    return false
}

fun isValidMaxLeave(maxLeave: String?): Boolean {
    if (maxLeave != null) {
        return maxLeave.isNotEmpty()
    }
    return false
}

fun isValidLeavePerYear(leavePerYear: String?): Boolean {
    if (leavePerYear != null) {
        return leavePerYear.isNotEmpty()
    }
    return false
}

fun isValidLeavePerMonth(leavePerMonth: String?): Boolean {
    if (leavePerMonth != null) {
        return leavePerMonth.isNotEmpty()
    }
    return false
}

fun isValidMinimumDaysWorked(minimumDaysWorked: String?): Boolean {
    if (minimumDaysWorked != null) {
        return minimumDaysWorked.isNotEmpty()
    }
    return false
}

fun isValidPermissionPerYear(permissionPerYear: String?): Boolean {
    if (permissionPerYear != null) {
        return permissionPerYear.isNotEmpty()
    }
    return false
}

fun isValidToleranceWorkTime(toleranceWorkTime: String?): Boolean {
    if (toleranceWorkTime != null) {
        return toleranceWorkTime.isNotEmpty()
    }
    return false
}

fun isValidCompanyWorkTime(companyWorkTime: String?): Boolean {
    if (companyWorkTime != null) {
        return companyWorkTime.isNotEmpty()
    }
    return false
}

fun isValidMaxCompensateWorkTime(maxCompensateWorkTime: String?): Boolean {
    if (maxCompensateWorkTime != null) {
        return maxCompensateWorkTime.isNotEmpty()
    }
    return false
}

fun isValidWifiSSID(wifiSSID: String?): Boolean {
    if (wifiSSID != null) {
        return wifiSSID.isNotEmpty()
    }
    return false
}
//

fun isValidEmail(email: String): Boolean {
    val emailRegex = Patterns.EMAIL_ADDRESS
    return emailRegex.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

fun isValidName(name: String): Boolean {
    return name.length >= 3
}


fun isValidLeaveRequestDateFrom(date: LocalDate): Boolean {
    val currentDate = LocalDate.now()
    return date.isAfter(currentDate) || date.isEqual(currentDate)
}

fun isValidLeaveRequestDateTo(dateFrom: LocalDate, dateTo: LocalDate): Boolean {
    return dateTo.isAfter(dateFrom) || dateTo.isEqual(dateFrom)
}

fun isValidCorrectionRequestDateFrom(date: LocalDate?, attendance: Attendance?): Boolean {
    if (date == null){
        return false
    }
    if (attendance == null) {
        return false
    }
    val attendanceDate = attendance.timein?.let { db_util.dateToLocalDate(it) } ?: return false
    return attendanceDate.isEqual(date)
}

//fun isValidCorrectionRequestDateTo(date: LocalDate, attendance: Attendance): Boolean {
//
//}