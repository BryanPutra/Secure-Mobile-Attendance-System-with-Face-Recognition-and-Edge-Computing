package com.example.Thesis_Project.ui.utils

import androidx.compose.ui.graphics.Color
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.Holiday
import com.example.Thesis_Project.backend.db.db_util
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


fun isAttended(attendance: Attendance?): Boolean {
    if (attendance == null) {
        return false
    }
    return attendance.absentflag == false && attendance.permissionflag == false && attendance.leaveflag == false && attendance.timeout != null
}

fun getAttendanceByDate(date: LocalDate?, attendances: List<Attendance>): Attendance? {
    if (date == null) {
        return null
    }
    return attendances.find { checkHaveSameDates(it.timein, db_util.localDateToDate(date)) }
}

fun getAttended(attendances: List<Attendance>): MutableList<Attendance>? {
    if (attendances.isEmpty()) {
        return null
    }

    val attendedAttendances = mutableListOf<Attendance>()

    for (attendance in attendances) {
        if (isAttended(attendance)) {
            attendedAttendances.add(attendance)
        }
    }
    return attendedAttendances

}

fun getUserMonthlyToleranceWorkTime(monthlyToleranceWorkTime: MutableMap<String, Int>?): String? {
    val currentMonthInt = LocalDate.now().monthValue
    if (monthlyToleranceWorkTime != null) {
        return "${monthlyToleranceWorkTime["$currentMonthInt"]} minutes"
    }
    return null
}

fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {
    return String.format("%02d Hours %02d Minutes", hours, minutes)
}

fun timeStringFromLong(milisecond: Long): String{
    val seconds = (milisecond / 1000) % 60
    val minutes = (milisecond / (1000 * 60) % 60)
    val hours = (milisecond / (1000 * 60 * 60) % 24)
    return makeTimeString(hours, minutes, seconds)
}

fun convertTimeMinutesIntToString(time: Int?): String {
    if (time != null) {
        val hours = time / 60
        val minutes = time % 60
        return "${hours}h ${minutes}m"
    }
    return ""
}

fun replaceTimeInDate(date: Date?, time: String?): Date? {
    if (date == null) {
        return null
    }
    if (time == null || time.isEmpty()) {
        return null
    }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.time = date
    val timeCalendar = Calendar.getInstance()
    timeCalendar.time = time.let { timeFormat.parse(it) } as Date

    calendar.apply {
        set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, 0)
    }
    return calendar.time
}

fun checkDateIsWeekend(date: Date?): Boolean {
    if (date == null){
        return false
    }
    val calendar = Calendar.getInstance()
    calendar.time = date
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
}

fun checkDateIsHoliday(date: LocalDate?, holidays: List<Holiday>?): Boolean {
    if (date == null){
        return false
    }
    if (holidays == null || holidays.isEmpty()) {
        return false
    }
    return holidays.any { it.date == db_util.localDateToDate(date) }
}

fun checkSixMonthsLeft(startDate: Date, endDate: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    val startYear = calendar.get(Calendar.YEAR)

    calendar.time = endDate
    val endYear = calendar.get(Calendar.YEAR)

    return (endYear - startYear == 0 && calendar.get(Calendar.MONTH) - Calendar.JUNE >= 6)
}

fun getEndOfYearDate(): Date{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, Calendar.DECEMBER)
    calendar.set(Calendar.DAY_OF_MONTH, 31)
    return calendar.time
}

fun convertHexToComposeColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#" + colorString))
}

fun checkLocalDateIsInCurrentMonth(date: LocalDate): Boolean {
    val currentMonth = LocalDate.now().month
    return date.month == currentMonth
}

fun checkHaveSameDates(date1: Date?, date2: Date?): Boolean{

    if (date1 == null || date2 == null){
        return false
    }

    val calendar1 = Calendar.getInstance().apply { time = date1 }
    val calendar2 = Calendar.getInstance().apply { time = date2 }

    val year1 = calendar1.get(Calendar.YEAR)
    val month1 = calendar1.get(Calendar.MONTH)
    val day1 = calendar1.get(Calendar.DAY_OF_MONTH)

    val year2 = calendar2.get(Calendar.YEAR)
    val month2 = calendar2.get(Calendar.MONTH)
    val day2 = calendar2.get(Calendar.DAY_OF_MONTH)

    return year1 == year2 && month1 == month2 && day1 == day2
}

fun formatStringDateToDate(date: String): Date? {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.parse(date)
}

fun formatDateToStringForInputs(date: Date?): String? {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return date?.let { dateFormat.format(it) }
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

    return "$month $dayOfMonth${getDayOfMonthOrdinal(dayOfMonth)} $year"
}

fun formatDateToStringWithDay(date: Date?): String? {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatDateToStringTimeOnly(date: Date?): String? {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return date?.let { dateFormat.format(it) }
}

fun formatLocalDateToString(date: LocalDate): String {
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
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