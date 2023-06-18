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
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.CalendarStatusItem
import com.example.Thesis_Project.ui.component_item_model.DayOfMonthItem
import com.example.Thesis_Project.ui.components.*
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

val daysString = listOf<String>("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

val calendarStatusList = listOf<CalendarStatusItem>(
    CalendarStatusItem(statusName = "Selected", statusColor = R.color.blue_500),
    CalendarStatusItem(statusName = "Attended", statusColor = R.color.teal_600),
    CalendarStatusItem(statusName = "Absent", statusColor = R.color.red_800),
    CalendarStatusItem(statusName = "Leave", statusColor = R.color.light_orange_300),
)

@Composable
fun CalendarScreen(navController: NavController? = null, mainViewModel: MainViewModel) {
    CalendarContainer(navController, mainViewModel)
}

@Composable
fun Calendar(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val currentDate = LocalDate.now()
    var monthYear by rememberSaveable { mutableStateOf(formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)) }
    val _daysInMonth = remember { mutableStateListOf<DayOfMonthItem>() }
    val daysInMonth = _daysInMonth

    fun onNextMonthClicked() {
        mainViewModel.calendarSelectedDate = mainViewModel.calendarSelectedDate.plusMonths(1)
        monthYear = formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)
    }

    fun onPreviousMonthClicked() {
        mainViewModel.calendarSelectedDate = mainViewModel.calendarSelectedDate.minusMonths(1)
        monthYear = formatMonthYearFromLocalDate(mainViewModel.calendarSelectedDate)
    }

    fun onDayClicked(dayOfMonth: DayOfMonthItem?, position: Int) {
        val scope = CoroutineScope(Dispatchers.Main)
        if (dayOfMonth == null) {
            return
        }
        dayOfMonth.isSelected = true
        mainViewModel.calendarSelectedDate = dayOfMonth.date!!
        val message =
            "Position = $position, clickedDay = ${dayOfMonth.dateString} " + "attendance = ${dayOfMonth.attendance} date = ${dayOfMonth.date == mainViewModel.calendarSelectedDate} " + "currentSelectedDate = ${mainViewModel.calendarSelectedDate}"
        scope.launch {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        mainViewModel.isRequestCorrectionButtonEnabled =
            !(dayOfMonth.date.isAfter(currentDate) || checkIfDAttendanceOnCorrectionPending(
                dayOfMonth.attendance,
                mainViewModel
            ))

        mainViewModel.isRequestLeaveButtonEnabled =
            !currentDate.isAfter(mainViewModel.calendarSelectedDate)
    }

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
        if (!_daysInMonth.isEmpty()) _daysInMonth.clear()
        for (i in daysInMonthList(mainViewModel.calendarSelectedDate)) {
            _daysInMonth.add(i)
        }
    }

    addDaysInMonth()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.spaceLarge), contentAlignment = Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
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
                modifier = Modifier.height(250.dp),
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
            ) {
                items(daysString) { dayString ->
                    Text(
                        text = dayString,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(letterSpacing = 2.sp)
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
                                checkIsLeaveOrPermission(dayOfMonth, mainViewModel)
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

    var isLaunched by rememberSaveable { mutableStateOf(false) }

    val firstDateOfMonth = rememberSaveable {
        mutableStateOf(db_util.firstDateOfMonth(mainViewModel.calendarSelectedDate))
    }
    val lastDateOfMonth = rememberSaveable {
        mutableStateOf(db_util.lastDateOfMonth(mainViewModel.calendarSelectedDate))
    }

    LaunchedEffect(Unit) {
        if (!isLaunched) {
            db_util.getAttendance(
                mainViewModel.db,
                mainViewModel.userData?.userid,
                firstDateOfMonth.value,
                lastDateOfMonth.value,
                mainViewModel.setAttendanceList
            )
            db_util.getCorrectionRequest(
                mainViewModel.db,
                mainViewModel.userData?.userid,
                mainViewModel.setCorrectionRequestList
            )
            isLaunched = true
        }
    }

    LaunchedEffect(mainViewModel.userData) {
        db_util.getAttendance(
            mainViewModel.db,
            mainViewModel.userData?.userid,
            firstDateOfMonth.value,
            lastDateOfMonth.value,
            mainViewModel.setAttendanceList
        )
        db_util.getCorrectionRequest(
            mainViewModel.db,
            mainViewModel.userData?.userid,
            mainViewModel.setCorrectionRequestList
        )
    }

    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentAttendance =
        getAttendanceByDate(mainViewModel.calendarSelectedDate, mainViewModel)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = "Bryan Putra",
            switchTabs = mainViewModel.switchHistoryTab,
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
                                text = formatLocalDateToStringDateOnly(mainViewModel.calendarSelectedDate) ?: "",
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = formatLocalDateToStringDayOnly(mainViewModel.calendarSelectedDate) ?: "",
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
                        text = convertTimeIntToString(currentAttendance?.worktime),
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
                        onClick = { mainViewModel.onRequestLeaveClicked() },
                        buttonText = "Request Leave",
                        isEnabled = mainViewModel.isRequestLeaveButtonEnabled
                    )
                }
                Box(modifier = Modifier.weight(0.5f)) {
                    ButtonHalfWidth(
                        onClick = { mainViewModel.onRequestCorrectionClicked() },
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
    if (mainViewModel.isCorrectionLeaveDialogShown) {
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