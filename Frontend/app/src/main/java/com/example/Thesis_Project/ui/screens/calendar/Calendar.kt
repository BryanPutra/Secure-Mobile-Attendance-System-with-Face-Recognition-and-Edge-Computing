package com.example.Thesis_Project.ui.screens.calendar

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.Thesis_Project.R
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.component_item_model.dayOfMonthItem
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

@Composable
fun CalendarScreen(navController: NavController? = null) {
    CalendarContainer(navController)
}

@Composable
fun dayCell() {
    val dayOfMonth by rememberSaveable { mutableStateOf("1") }
    Box() {
        Text(dayOfMonth, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun Calendar() {
    val context = LocalContext.current
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val monthYear by rememberSaveable { mutableStateOf(formatMonthYearFromDate(selectedDate)) }
    fun onNextMonthClicked() {
        selectedDate = selectedDate.plusMonths(1)
    }

    fun onPreviousMonthClicked() {
        selectedDate = selectedDate.minusMonths(1)
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

        for (i in 1 until 42) {
            if (i <= dayOfWeek || i > daysInMonthAmount + dayOfWeek) {
                daysInMonthList.add("")
            } else {
                daysInMonthList.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthList
    }
    daysInMonthList(selectedDate)
    val _daysInMonth = remember { mutableStateListOf<String>() }
    val daysInMonth = _daysInMonth

    for (i in daysInMonthList(selectedDate)) {
        _daysInMonth.add(i)
    }
//    val daysOfMonth = rememberSaveable { mutableStateListOf<dayOfMonthItem>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.spaceLarge),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
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
                        .size(25.dp)
                        .padding(MaterialTheme.spacing.spaceSmall),
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
                        modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                    )
                }
                Text(text = monthYear)
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
                        modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysString.forEach { day ->
                    Text(text = day, fontWeight = FontWeight.Bold)
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                itemsIndexed(daysInMonth) { index, dayOfMonth ->
                    Text(
                        text = dayOfMonth,
                        modifier = Modifier.clickable { onDayClicked(dayOfMonth, index) })
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
        Calendar()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        Calendar()
    }
}