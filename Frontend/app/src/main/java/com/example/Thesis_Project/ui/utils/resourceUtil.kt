package com.example.Thesis_Project.ui.utils

import androidx.compose.ui.graphics.Color
import com.example.Thesis_Project.backend.db.db_models.Attendance
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


fun isAttended(attendance: Attendance): Boolean {
    return attendance.absentflag == false && attendance.permissionflag == false && attendance.leaveflag == false && attendance.timeout != null
}

fun convertTimeIntToString(time: Int?): String {
    if (time != null) {
        val hours = time / 60
        val minutes = time % 60
        return "${hours}h ${minutes}m"
    }
    return ""
}

fun convertHexToComposeColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}

fun formatDateToString(date: Date?): String? {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatDateToStringWithOrdinal(date: Date?): String? {
    val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
    val yearFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)

    val month = date?.let { monthFormat.format(it) }
    val year = date?.let { yearFormat.format(it) }

    var dayOfMonth: Int = 0
    if (date != null) {
        dayOfMonth = getDayOfMonthFromDate(date)
    }

    return "$month ${getDayOfMonthOrdinal(dayOfMonth)} $year"
}

fun formatDateToStringWithDay(date: Date?): String? {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatDateToStringTimeOnly(date: Date?): String? {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatLocalDateToString(date: LocalDate): String {
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM//yyyy", Locale.ENGLISH)
    return date.format(dateFormat)
}

fun formatLocalDateToStringDateOnly(date: LocalDate): String {
    val dateFormat = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH)
    return date.format(dateFormat)
}

fun formatLocalDateToStringDayOnly(date: LocalDate): String {
    val dateFormat = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    return date.format(dateFormat)
}

fun formatMonthYearFromLocalDate(date: LocalDate?): String? {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    if (date != null) {
        return date.format(formatter)
    }
    return ""
}

fun getListOfAttendancesByMonth(attendances: List<Attendance>, month: Int): MutableList<Attendance>? {
    if (attendances.isEmpty()){
        return null
    }
    val attendedAttendances = mutableListOf<Attendance>()
    val filteredAttendances = mutableListOf<Attendance>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM" , Locale.ENGLISH)

    for (attendance in attendances) {
        if (isAttended(attendance)){
            attendedAttendances.add(attendance)
        }
    }

    for (attendance in attendedAttendances) {
        calendar.time = attendance.timeout!!
        val dateMonth = dateFormat.format(calendar.time).toInt()
        if (dateMonth == month) {
            filteredAttendances.add(attendance)
        }
    }
    return filteredAttendances
}

fun getDayOfMonthFromDate(date: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun getDayOfMonthOrdinal(dayOfMonth: Int): String {
    if (dayOfMonth in 11..13) {
        return "th"
    }
    return when (dayOfMonth % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}