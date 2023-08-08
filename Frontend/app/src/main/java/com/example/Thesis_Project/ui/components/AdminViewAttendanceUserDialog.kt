package com.example.Thesis_Project.ui.components

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate


@Composable
fun AdminViewAttendanceUserDialog(
    mainViewModel: MainViewModel,
    user: User?,
    onCancelClicked: () -> Unit
) {

    val getAttendanceCurrentMonthScope = rememberCoroutineScope()

    var currentSelectedDate by rememberSaveable {
        mutableStateOf(LocalDate.now())
    }

    var firstDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.firstDateOfMonth(currentSelectedDate))
    }
    var lastDateOfMonth by rememberSaveable {
        mutableStateOf(db_util.lastDateOfMonth(currentSelectedDate))
    }

    var monthYear by rememberSaveable {
        mutableStateOf(
            formatMonthYearFromLocalDate(
                currentSelectedDate
            )
        )
    }

    var currentMonthAttendanceList: List<Attendance>? by rememberSaveable { mutableStateOf(null) }
    val setCurrentMonthAttendanceList: (List<Attendance>?) -> Unit = { newMonthAttendanceList ->
        if (newMonthAttendanceList != null) {
            currentMonthAttendanceList = newMonthAttendanceList
            Log.d("currentMonthAttendance", "currentMonthAttendance: $currentMonthAttendanceList")
        } else {
            Log.d("get currentMonthAttendance", "currentMonthAttendance null")
        }
    }

    var attendanceQuerySearch by rememberSaveable { mutableStateOf("") }
    var searchIsActive by rememberSaveable { mutableStateOf(false) }
    val lastAttendanceItemIndex by rememberSaveable { mutableStateOf(currentMonthAttendanceList?.size) }
    val searchedItems = remember { mutableStateListOf<String>() }
    val filteredAttendanceQuery = remember { mutableStateListOf<Attendance>() }

    var selectedViewAttendance by remember { mutableStateOf<Attendance?>(null) }
    var viewAttendanceDetailDialogShown by rememberSaveable { mutableStateOf(false) }

    fun appendAttendanceList() {
        if (currentMonthAttendanceList != null) {
            filteredAttendanceQuery.clear()
            for (i in currentMonthAttendanceList!!) {
                filteredAttendanceQuery.add(i)
            }
        }
    }

    fun searchAttendance(searchValue: String) {
        filteredAttendanceQuery.clear()
        if (searchValue.isEmpty()) {
            appendAttendanceList()
            return
        }
        val tempFilteredUsers =
            currentMonthAttendanceList?.filter {
                formatDateToStringWithOrdinal(it.timein)?.lowercase()
                    ?.contains(searchValue.lowercase()) ?: false
            }
        if (tempFilteredUsers != null) {
            for (i in tempFilteredUsers) {
                filteredAttendanceQuery.add(i)
            }
        }
        searchedItems.add(attendanceQuerySearch)
        attendanceQuerySearch = ""
    }

    fun onNextMonthClicked() {
        currentSelectedDate = currentSelectedDate.plusMonths(1)
        firstDateOfMonth = db_util.firstDateOfMonth(currentSelectedDate)
        lastDateOfMonth = db_util.lastDateOfMonth(currentSelectedDate)
        monthYear = formatMonthYearFromLocalDate(currentSelectedDate)
        runBlocking {
            getAttendanceCurrentMonthScope.launch {
                mainViewModel.setIsLoading(true)
                db_util.getAttendance(
                    mainViewModel.db,
                    user?.userid,
                    firstDateOfMonth,
                    lastDateOfMonth,
                    setCurrentMonthAttendanceList
                )
                appendAttendanceList()
                mainViewModel.setIsLoading(false)
            }
        }
    }

    fun onPreviousMonthClicked() {
        currentSelectedDate = currentSelectedDate.minusMonths(1)
        firstDateOfMonth = db_util.firstDateOfMonth(currentSelectedDate)
        lastDateOfMonth = db_util.lastDateOfMonth(currentSelectedDate)
        monthYear = formatMonthYearFromLocalDate(currentSelectedDate)
        runBlocking {
            getAttendanceCurrentMonthScope.launch {
                mainViewModel.setIsLoading(true)
                db_util.getAttendance(
                    mainViewModel.db,
                    user?.userid,
                    firstDateOfMonth,
                    lastDateOfMonth,
                    setCurrentMonthAttendanceList
                )
                appendAttendanceList()
                mainViewModel.setIsLoading(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        runBlocking {
            getAttendanceCurrentMonthScope.launch {
                mainViewModel.setIsLoading(true)
                db_util.getAttendance(
                    mainViewModel.db,
                    user?.userid,
                    firstDateOfMonth,
                    lastDateOfMonth,
                    setCurrentMonthAttendanceList
                )
                appendAttendanceList()
                mainViewModel.setIsLoading(false)
            }
        }
    }

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(MaterialTheme.spacing.spaceLarge),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(
                    id = R.color.white
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
        ) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.spaceLarge)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Text(
                    text = user?.name ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchBar(
                        query = attendanceQuerySearch,
                        onQueryChange = { attendanceQuerySearch = it },
                        onSearch = {
                            searchAttendance(attendanceQuerySearch)
                            searchIsActive = false
                        },
                        active = searchIsActive,
                        onActiveChange = {
                            searchIsActive = it
                        },
                        placeholder = {
                            Text(text = "Search attendance by date", fontSize = 11.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchIsActive) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        if (attendanceQuerySearch.isNotEmpty()) {
                                            attendanceQuerySearch = ""
                                        } else {
                                            searchIsActive = false
                                        }
                                    },
                                    imageVector = Icons.Filled.HighlightOff,
                                    contentDescription = "close search"
                                )
                            }
                        }
                    )
                    {
                        searchedItems.forEach {
                            Row(
                                modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
                                horizontalArrangement = Arrangement.Absolute.spacedBy(MaterialTheme.spacing.spaceMedium)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = "search history"
                                )
                                Text(text = it)
                            }
                        }
                    }
                    if (currentMonthAttendanceList != null && currentMonthAttendanceList!!.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
                            modifier = Modifier.height(250.dp)
                        ) {
                            itemsIndexed(filteredAttendanceQuery) { index, attendanceItem ->
                                AdminViewAttendanceRow(
                                    mainViewModel = mainViewModel,
                                    attendance = attendanceItem,
                                    onViewClick = { viewItem ->
                                        selectedViewAttendance = viewItem
                                        viewAttendanceDetailDialogShown = true
                                    })
                                if (index != lastAttendanceItemIndex?.minus(1)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(colorResource(id = R.color.gray_400))
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Close")
                    }
                }
            }
        }
        if (viewAttendanceDetailDialogShown) {
            AdminViewAttendanceDetailUserDialog(
                mainViewModel = mainViewModel,
                attendance = selectedViewAttendance
            ) {
                selectedViewAttendance = null
                viewAttendanceDetailDialogShown = false
            }
        }
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
    }
}

