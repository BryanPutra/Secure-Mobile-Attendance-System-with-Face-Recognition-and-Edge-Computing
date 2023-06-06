package com.example.Thesis_Project.ui.screens.calendar

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.R
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.CalendarStatusItem
import com.example.Thesis_Project.ui.components.CalendarStatus
import com.example.Thesis_Project.ui.components.MainHeader
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.ui.utils.formatMonthYearFromDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

val daysString = listOf<String>("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

val calendarStatusList = listOf<CalendarStatusItem>(
    CalendarStatusItem(statusName = "Selected", statusColor = R.color.blue_500),
    CalendarStatusItem(statusName = "Attended", statusColor = R.color.teal_600),
    CalendarStatusItem(statusName = "Absent", statusColor = R.color.red_800),
    CalendarStatusItem(statusName = "Leave", statusColor = R.color.light_orange_300),
)

@Composable
fun CalendarScreen(navController: NavController? = null) {
    CalendarContainer(navController)
}

@Composable
fun Calendar() {
    val context = LocalContext.current
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var monthYear by rememberSaveable { mutableStateOf(formatMonthYearFromDate(selectedDate)) }
    val _daysInMonth = remember { mutableStateListOf<String>() }
    val daysInMonth = _daysInMonth

    fun onNextMonthClicked() {
        selectedDate = selectedDate.plusMonths(1)
        monthYear = formatMonthYearFromDate(selectedDate)
    }

    fun onPreviousMonthClicked() {
        selectedDate = selectedDate.minusMonths(1)
        monthYear = formatMonthYearFromDate(selectedDate)
    }

    fun onDayClicked(dayOfMonth: String, position: Int) {
        val scope = CoroutineScope(Dispatchers.Main)
        if (!dayOfMonth.isEmpty()) {
            val message = "Position = $position, clickedDay = $dayOfMonth"
            scope.launch {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun daysInMonthList(date: LocalDate): SnapshotStateList<String> {
        val daysInMonthList = SnapshotStateList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonthAmount = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1 until 35) {
            if (i <= dayOfWeek || i > daysInMonthAmount + dayOfWeek) {
                daysInMonthList.add("")
            } else {
                daysInMonthList.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthList
    }

    fun addDaysInMonth() {
        if (!_daysInMonth.isEmpty()) _daysInMonth.clear()
        for (i in daysInMonthList(selectedDate)) {
            _daysInMonth.add(i)
        }
    }
    addDaysInMonth()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.spaceLarge),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .size(25.dp),
                    onClick = { onPreviousMonthClicked() },
                    border = BorderStroke(
                        2.dp, colorResource(
                            id = R.color.black
                        )
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = null,
                        tint = colorResource(
                            id = R.color.black
                        ),
                        modifier = Modifier.size(MaterialTheme.spacing.iconExtraSmall)
                    )
                }
                Text(text = monthYear, style = MaterialTheme.typography.titleLarge)
                OutlinedButton(
                    modifier = Modifier.size(25.dp),
                    onClick = { onNextMonthClicked() },
                    border = BorderStroke(
                        2.dp, colorResource(
                            id = R.color.black
                        )
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward, contentDescription = null,
                        tint = colorResource(
                            id = R.color.black
                        ),
                        modifier = Modifier.size(MaterialTheme.spacing.iconExtraSmall)
                    )
                }
            }
            LazyVerticalGrid(
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
                        text = dayOfMonth,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onDayClicked(dayOfMonth, index) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarContainer(navController: NavController? = null) {
    var date by rememberSaveable { mutableStateOf("") }
    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val destination = currentBackStackEntry?.destination
    val currentRoute = currentBackStackEntry?.destination?.route
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainHeader(
            page = currentRoute,
            userFullName = "Bryan Putra",
            onCorrectionSelected = {
            },
            onLeaveSelected = {
            }
        )
        Column(
            modifier = Modifier
                .offset(y = (-75).dp)
                .fillMaxSize()
                .padding(MaterialTheme.spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(
                        id = R.color.white
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
            ) {
                Calendar()
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
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        CalendarContainer()
    }
}