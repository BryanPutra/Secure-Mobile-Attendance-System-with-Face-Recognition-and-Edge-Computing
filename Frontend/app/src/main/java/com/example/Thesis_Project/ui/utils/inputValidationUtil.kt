package com.example.Thesis_Project.ui.utils

import android.util.Patterns
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    return (date.isAfter(currentDate) || date.isEqual(currentDate)) && checkLocalDateIsInCurrentMonth(date)
}

fun isValidLeaveRequestDateTo(dateFrom: LocalDate, dateTo: LocalDate): Boolean {
    return (dateTo.isAfter(dateFrom) || dateTo.isEqual(dateFrom)) && checkLocalDateIsInCurrentMonth(dateTo)
}

fun isValidLeaveRequestDateLeavePermission(date: LocalDate): Boolean {
    return checkLocalDateIsInCurrentMonth(date)
}

fun isValidDetailRequest(detail: String): Boolean {
    return detail.isNotEmpty()
}

fun isValidPresentTimeIn(time: String?, companyTimeIn: String?, companyTimeOut: String?): Boolean{
    if (time == null || time.isEmpty()){
        return false
    }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val localTime = LocalTime.parse(time, formatter)
    val localTimeIn = LocalTime.parse(companyTimeIn, formatter)
    val localTimeOut = LocalTime.parse(companyTimeOut, formatter)
    return localTime.isAfter(localTimeIn) && localTime.isBefore(localTimeOut)
}
fun isValidTimeOut(timeIn: String?, timeOut: String?): Boolean {
    if (timeIn == null || timeOut == null){
        return false
    }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val localTimeIn = LocalTime.parse(timeIn, formatter)
    val localTimeOut = LocalTime.parse(timeOut, formatter)
    return localTimeOut.isAfter(localTimeIn)
}