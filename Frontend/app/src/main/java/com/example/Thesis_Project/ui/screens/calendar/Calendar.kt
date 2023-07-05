package com.example.Thesis_Project.ui.screens.calendar

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.CalendarStatusItem
import com.example.Thesis_Project.ui.component_item_model.DayOfMonthItem
import com.example.Thesis_Project.ui.components.*
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

val daysString = listOf<String>("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

val calendarStatusList = listOf<CalendarStatusItem>(
    CalendarStatusItem(statusName = "Selected", statusColor = R.color.blue_500),
    CalendarStatusItem(statusName = "Attended", statusColor = R.color.teal_600),
    CalendarStatusItem(statusName = "Absent", statusColor = R.color.red_800),
    CalendarStatusItem(statusName = "Leave", statusColor = R.color.purple_500),
)

@Composable
fun CalendarScreen(navController: NavController? = null, mainViewModel: MainViewModel) {
    CalendarContainer(navController, mainViewModel)
}

@Composable
fun Calendar(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val getAttendanceCurrentMonthScope = rememberCoroutineScope()

    val currentDate = LocalDate.now()
    var firstDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.firstDateOfMonth(mainViewModel.calendarSelectedDate))
    }
    var lastDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.lastDateOfMonth(mainViewModel.calendarSelectedDate))
    }
    var monthYear by rememberSaveable { mutableStateOf(formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)) }
    val daysInMonth = remember { mutableStateListOf<DayOfMonthItem>() }

    fun daysInMonthList(date: LocalDate): SnapshotStateList<DayOfMonthItem> {
        val year = date.year
        val month = date.month
        val daysInMonthList = SnapshotStateList<DayOfMonthItem>()
        val datesInMonthList = mutableListOf<LocalDate>()
        val yearMonth = YearMonth.from(date)
        val daysInMonthAmount = yearMonth.lengthOfMonth()
        val firstOfMonth = mainViewModel.calendarSelectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth?.dayOfWeek?.value

        for (day in 1..daysInMonthAmount) {
            datesInMonthList.add(LocalDate.of(year, month, day))
        }

        for (i in 1 until 35) {
            if (i <= dayOfWeek!! || i > daysInMonthAmount + dayOfWeek) {
                daysInMonthList.add(DayOfMonthItem(date = null, dateString = "", attendance = null))
            } else {
                daysInMonthList.add(
                    DayOfMonthItem(
                        date = datesInMonthList[i - dayOfWeek - 1],
                        dateString = (i - dayOfWeek).toString(),
                        attendance = getAttendanceByDay((i - dayOfWeek).toString(), mainViewModel)
                    )
                )
            }
        }
        return daysInMonthList
    }

    fun addDaysInMonth() {
        if (!daysInMonth.isEmpty()) daysInMonth.clear()
        for (i in daysInMonthList(mainViewModel.calendarSelectedDate)) {
            daysInMonth.add(i)
        }
    }

    suspend fun getAttendanceCurrentMonth() {
        coroutineScope {
            launch {
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    firstDateOfMonth,
                    lastDateOfMonth,
                    mainViewModel.setAttendanceList
                )
            }
        }
    }

    fun onNextMonthClicked() {
        mainViewModel.setCalendarSelectedDate(mainViewModel.calendarSelectedDate.plusMonths(1))
        firstDateOfMonth = db_util.firstDateOfMonth(mainViewModel.calendarSelectedDate)
        lastDateOfMonth = db_util.lastDateOfMonth(mainViewModel.calendarSelectedDate)
        monthYear = formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)
        runBlocking {
            getAttendanceCurrentMonthScope.launch {
                getAttendanceCurrentMonth()
                addDaysInMonth()
                val tempDayOfMonth = daysInMonth.toList().find { it.date?.isEqual(mainViewModel.calendarSelectedDate) ?: false }
                if (tempDayOfMonth != null){
                    mainViewModel.setIsRequestCorrectionButtonEnabled(
                        tempDayOfMonth.attendance != null && (!(tempDayOfMonth.date?.isEqual(currentDate) == true || tempDayOfMonth.date?.isAfter(
                            currentDate
                        ) == true || checkIfAttendanceOnCorrectionPending(
                            tempDayOfMonth.attendance,
                            mainViewModel
                        ))) && mainViewModel.attendanceList?.let {
                            getAttendanceByDate(
                                mainViewModel.calendarSelectedDate,
                                it
                            )?.timeout
                        } != null
                    )
                    mainViewModel.setIsRequestLeaveButtonEnabled(!currentDate.isAfter(mainViewModel.calendarSelectedDate) && mainViewModel.attendanceList?.let {
                        getAttendanceByDate(
                            mainViewModel.calendarSelectedDate,
                            it
                        )?.timein
                    } == null && (if (tempDayOfMonth.attendance == null) { !checkDateIsWeekend(tempDayOfMonth.date?.let {
                        db_util.localDateToDate(
                            it
                        )
                    }) && !checkDateIsHoliday(tempDayOfMonth.date, mainViewModel.holidaysList) } else false) )
                }
            }

        }

        Log.d("nextMonth", "$daysInMonth")
    }

    fun onPreviousMonthClicked() {
        mainViewModel.setCalendarSelectedDate(mainViewModel.calendarSelectedDate.minusMonths(1))
        firstDateOfMonth = db_util.firstDateOfMonth(mainViewModel.calendarSelectedDate)
        lastDateOfMonth = db_util.lastDateOfMonth(mainViewModel.calendarSelectedDate)
        monthYear = formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)

        runBlocking {
            getAttendanceCurrentMonthScope.launch {
                getAttendanceCurrentMonth()
                addDaysInMonth()
                val tempDayOfMonth = daysInMonth.toList().find { it.date?.isEqual(mainViewModel.calendarSelectedDate) ?: false }
                if (tempDayOfMonth != null){
                    mainViewModel.setIsRequestCorrectionButtonEnabled(
                        tempDayOfMonth.attendance != null && (!(tempDayOfMonth.date?.isEqual(currentDate) == true || tempDayOfMonth.date?.isAfter(
                            currentDate
                        ) == true || checkIfAttendanceOnCorrectionPending(
                            tempDayOfMonth.attendance,
                            mainViewModel
                        ))) && mainViewModel.attendanceList?.let {
                            getAttendanceByDate(
                                mainViewModel.calendarSelectedDate,
                                it
                            )?.timeout
                        } != null
                    )
                    mainViewModel.setIsRequestLeaveButtonEnabled(!currentDate.isAfter(mainViewModel.calendarSelectedDate) && mainViewModel.attendanceList?.let {
                        getAttendanceByDate(
                            mainViewModel.calendarSelectedDate,
                            it
                        )?.timein
                    } == null && (if (tempDayOfMonth.attendance == null) { !checkDateIsWeekend(tempDayOfMonth.date?.let {
                        db_util.localDateToDate(
                            it
                        )
                    }) && !checkDateIsHoliday(tempDayOfMonth.date, mainViewModel.holidaysList) } else false) )
                }
            }
        }
        Log.d("previousMonth", "${daysInMonth[0]}")
    }

    fun onDayClicked(dayOfMonth: DayOfMonthItem?, position: Int) {
        if (dayOfMonth == null) {
            Log.d("DayOfMonth Clicked", "$dayOfMonth")
            return
        }
        dayOfMonth.isSelected = true
        mainViewModel.setCalendarSelectedDate(dayOfMonth.date)
//        val message =
//            "Position = $position, clickedDay = ${dayOfMonth.dateString} " + "attendance = ${dayOfMonth.attendance} date = ${dayOfMonth.date} " + "currentSelectedDate = ${mainViewModel.calendarSelectedDate}"
//        scope.launch {
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//        }

        mainViewModel.setIsRequestCorrectionButtonEnabled(
            dayOfMonth.attendance != null && (!(dayOfMonth.date?.isEqual(currentDate) == true || dayOfMonth.date?.isAfter(
                currentDate
            ) == true || checkIfAttendanceOnCorrectionPending(
                dayOfMonth.attendance,
                mainViewModel
            ))) && mainViewModel.attendanceList?.let {
                getAttendanceByDate(
                    mainViewModel.calendarSelectedDate,
                    it
                )?.timeout
            } != null
        )

        mainViewModel.setIsRequestLeaveButtonEnabled(!currentDate.isAfter(mainViewModel.calendarSelectedDate) && mainViewModel.attendanceList?.let {
            getAttendanceByDate(
                mainViewModel.calendarSelectedDate,
                it
            )?.timein
        } == null && (if (dayOfMonth.attendance == null) { !checkDateIsWeekend(dayOfMonth.date?.let {
            db_util.localDateToDate(
                it
            )
        }) && !checkDateIsHoliday(dayOfMonth.date, mainViewModel.holidaysList) } else false) )
    }
    addDaysInMonth()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.spaceLarge), contentAlignment = Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    modifier = Modifier.size(25.dp),
                    onClick = { onPreviousMonthClicked() },
                    border = BorderStroke(2.dp, colorResource(id = R.color.black)),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(MaterialTheme.spacing.iconExtraSmall)
                    )
                }
                Text(text = monthYear ?: "", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(
                    modifier = Modifier.size(25.dp),
                    onClick = { onNextMonthClicked() },
                    border = BorderStroke(2.dp, colorResource(id = R.color.black)),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(MaterialTheme.spacing.iconExtraSmall)
                    )
                }
            }
            LazyVerticalGrid(
                modifier = Modifier.height(275.dp),
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
            ) {
                items(daysString) { dayString ->
                    Text(
                        text = dayString,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(letterSpacing = 1.sp)
                    )
                }
                itemsIndexed(daysInMonth) { index, dayOfMonth ->
                    Text(
                        text = dayOfMonth.dateString,
                        textAlign = TextAlign.Center,
                        color = colorResource(
                            setDateTextColor(
                                checkIsSelected(dayOfMonth, mainViewModel),
                                checkIsAttended(dayOfMonth, mainViewModel),
                                checkIsAbsent(dayOfMonth, mainViewModel),
                                checkIsLeaveOrPermission(dayOfMonth, mainViewModel),
                                checkIsWeekend(dayOfMonth),
                                checkDateIsHoliday(dayOfMonth.date, mainViewModel.holidaysList)
                            )
                        ),
                        modifier = Modifier
                            .clickable { onDayClicked(dayOfMonth, index) }
                            .padding(MaterialTheme.spacing.spaceExtraSmall)
                            .drawBehind {
                                if (mainViewModel.calendarSelectedDate == dayOfMonth.date) {
                                    drawCircle(
                                        color = convertHexToComposeColor("1e90ff"),
                                        radius = this.size.maxDimension / 2
                                    )
                                }
                            })
                }
            }
        }
    }
}

@Composable
fun CalendarContainer(navController: NavController? = null, mainViewModel: MainViewModel) {

    val currentDate = LocalDate.now()
    val currentMonth = currentDate.month
    val isLaunched by rememberSaveable { mutableStateOf(mainViewModel.isCalendarInit) }

    val firstDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.firstDateOfMonth(mainViewModel.calendarSelectedDate))
    }
    val lastDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.lastDateOfMonth(mainViewModel.calendarSelectedDate))
    }

    suspend fun getInitData() {
        coroutineScope {
            launch {
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    firstDateOfMonth,
                    lastDateOfMonth,
                ) { attendanceList ->
                    mainViewModel.setAttendanceList(attendanceList)
                }
                mainViewModel.setIsRequestLeaveButtonEnabled(!currentDate.isAfter(mainViewModel.calendarSelectedDate) && mainViewModel.attendanceList?.let {
                    getAttendanceByDate(
                        mainViewModel.calendarSelectedDate,
                        it
                    )?.timein
                } == null && !checkDateIsWeekend(db_util.localDateToDate(mainViewModel.calendarSelectedDate)) && !checkDateIsHoliday(
                    db_util.dateToLocalDate(
                        db_util.localDateToDate(mainViewModel.calendarSelectedDate)
                    ), mainViewModel.holidaysList
                ))
                mainViewModel.setIsRequestCorrectionButtonEnabled(mainViewModel.attendanceList?.let {
                    getAttendanceByDate(
                        mainViewModel.calendarSelectedDate,
                        it
                    )?.timeout
                } != null
                )
            }
            launch {
                db_util.getCorrectionRequest(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    mainViewModel.setCorrectionRequestList
                )
            }
            launch {
                db_util.getLeaveRequest(
                    mainViewModel.db,
                    mainViewModel.userData?.userid,
                    mainViewModel.setLeaveRequestList
                )
            }
            launch {
                db_util.getHolidays(
                    mainViewModel.db,
                    null,
                    mainViewModel.setHolidayList
                )
            }
        }
    }

    LaunchedEffect(key1 = isLaunched) {
        if (!isLaunched) {
            runBlocking {
                getInitData()
                mainViewModel.setIsCalendarInit(true)
            }
        }
    }

    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentAttendance =
        getAttendanceByDate(mainViewModel.calendarSelectedDate, mainViewModel)

    var requestReminderDialogShown by rememberSaveable { mutableStateOf(false) }

    fun onRequestLeaveClicked(){
        if (mainViewModel.calendarSelectedDate.month == currentMonth){
            mainViewModel.onRequestLeaveClicked()
        }
        else {
            requestReminderDialogShown = true
        }
    }

    fun onRequestCorrectionClicked(){
        if (mainViewModel.calendarSelectedDate.month == currentMonth){
            mainViewModel.onRequestCorrectionClicked()
        }
        else {
            requestReminderDialogShown = true
        }
    }

    if (requestReminderDialogShown) {
        AlertDialog(
            onDismissRequest = { requestReminderDialogShown = false },
            title = { Text(text = "Reminder") },
            text = { Text(text = "You can only request leave and correction in the current month") },
            confirmButton = {
                Button(
                    onClick = {
                        requestReminderDialogShown = false
                    }
                ) {
                    Text(text = "Dismiss")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = mainViewModel.userData?.name,
            mainViewModel = mainViewModel
        )
        Column(
            modifier = Modifier
                .offset(y = (-75).dp)
                .fillMaxSize()
                .padding(MaterialTheme.spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
            ) {
                Calendar(mainViewModel)
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(calendarStatusList) { calendarStatusItem ->
                    CalendarStatus(calendarStatusItem)
                }
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
            ) {
                item {
                    Text(
                        text = "Date",
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.gray_400)
                    )
                }
                item {
                    Text(
                        text = "Tap In",
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.gray_400)
                    )
                }
                item {
                    Text(
                        text = "Tap Out",
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.gray_400)
                    )
                }
                item {
                    Text(
                        text = "Duration",
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.gray_400)
                    )
                }
                item {
                    Box(contentAlignment = Alignment.Center) {
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceXXSmall)) {
                            Text(
                                text = formatLocalDateToStringDateOnly(mainViewModel.calendarSelectedDate),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = formatLocalDateToStringDayOnly(mainViewModel.calendarSelectedDate),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = formatDateToStringTimeOnly(currentAttendance?.timein) ?: "",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
                item {
                    Text(
                        text = formatDateToStringTimeOnly(currentAttendance?.timeout) ?: "",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
                item {
                    Text(
                        text = convertTimeMinutesIntToString(currentAttendance?.worktime),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.5f)) {
                    ButtonHalfWidth(
                        onClick = { onRequestLeaveClicked() },
                        buttonText = "Request Leave",
                        isEnabled = mainViewModel.isRequestLeaveButtonEnabled
                    )
                }
                Box(modifier = Modifier.weight(0.5f)) {
                    ButtonHalfWidth(
                        onClick = { onRequestCorrectionClicked() },
                        buttonText = "Request Correction",
                        isEnabled = mainViewModel.isRequestCorrectionButtonEnabled
                    )
                }
            }
        }
    }
    if (mainViewModel.isRequestLeaveDialogShown) {
        LeaveRequestDialog(mainViewModel = mainViewModel)
    }
    if (mainViewModel.isCorrectionDialogShown) {
        CorrectionRequestDialog(mainViewModel = mainViewModel, currentAttendance)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        CalendarContainer()
//    }
//}