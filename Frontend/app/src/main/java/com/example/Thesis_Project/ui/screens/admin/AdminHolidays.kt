package com.example.Thesis_Project.ui.screens.admin

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Holiday
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.AdminCreateHolidayDialog
import com.example.Thesis_Project.ui.components.AdminCreateUserDialog
import com.example.Thesis_Project.ui.components.AdminHolidaysRow
import com.example.Thesis_Project.ui.components.CircularLoadingBar
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.*

@Composable
fun AdminHolidaysScreen(navController: NavController, mainViewModel: MainViewModel) {
    AdminHolidaysContainer(navController = navController, mainViewModel = mainViewModel)
}

@Composable
fun AdminHolidaysContainer(navController: NavController, mainViewModel: MainViewModel) {
    val isLaunched by rememberSaveable { mutableStateOf(mainViewModel.isAdminHolidaysInit) }
    val deleteHolidayScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var holidayQuerySearch by rememberSaveable { mutableStateOf("") }
    var searchIsActive by rememberSaveable { mutableStateOf(false) }
    val searchedItems = remember { mutableStateListOf<String>() }
    val filteredHolidayQuery = remember { mutableStateListOf<Holiday>() }
    val lastHolidayItemIndex by rememberSaveable { mutableStateOf((mainViewModel.holidaysList?.size)) }

    var selectedHoliday by remember { mutableStateOf<Holiday?>(null) }

    var deleteHolidayConfirmDialogShown by rememberSaveable { mutableStateOf(false) }

    fun appendHolidaysList() {
        if (mainViewModel.holidaysList != null) {
            filteredHolidayQuery.clear()
            for (i in mainViewModel.holidaysList!!) {
                filteredHolidayQuery.add(i)
            }
        }
    }

    LaunchedEffect(key1 = isLaunched) {
        if (!isLaunched) {
            db_util.getHolidays(mainViewModel.db, null, mainViewModel.setHolidayList)
            appendHolidaysList()
            mainViewModel.setIsAdminHolidaysInit(true)
        }
    }

    LaunchedEffect(mainViewModel.holidaysList) {
        appendHolidaysList()
    }

    fun searchHolidays(searchValue: String) {
        filteredHolidayQuery.clear()
        if (searchValue.isEmpty()) {
            appendHolidaysList()
            return
        }

        val tempFilteredHolidays =
            mainViewModel.holidaysList?.filter {
                it.holidayname?.lowercase()?.contains(searchValue.lowercase()) ?: false
            }
        if (tempFilteredHolidays != null) {
            for (i in tempFilteredHolidays) {
                filteredHolidayQuery.add(i)
            }
        }
        searchedItems.add(holidayQuerySearch)
        holidayQuerySearch = ""
    }

    val postDeleteHoliday: suspend (holiday: Holiday) -> Unit = { holiday ->
        mainViewModel.setIsLoading(true)
        try {
            db_util.deleteHolidayManual(mainViewModel.db, holiday.holidayid!!)
            db_util.getHolidays(mainViewModel.db, null, mainViewModel.setHolidayList)
            mainViewModel.showToast(context, "Holiday has been deleted successfully")
        } catch (e: Exception) {
            mainViewModel.showToast(context, "Failed to delete holiday: ${e.message}")
            Log.e("Error", "Failed to to delete holiday: $e")
        }
        selectedHoliday = null
        mainViewModel.setIsLoading(false)
    }

    fun onConfirmDeleteHolidayClicked() {
        Log.d("checkselectedholiday", "$selectedHoliday")
        deleteHolidayScope.launch {
            postDeleteHoliday(selectedHoliday!!)
        }
    }

    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Holidays",
                style = MaterialTheme.typography.headlineSmall,
            )
            Icon(
                modifier = Modifier
                    .size(MaterialTheme.spacing.iconLarge)
                    .clickable {
                        mainViewModel.toggleCreateHolidayDialog()
                    },
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = colorResource(id = R.color.blue_500)
            )
        }
        SearchBar(
            query = holidayQuerySearch,
            onQueryChange = { holidayQuerySearch = it },
            onSearch = {
                searchHolidays(holidayQuerySearch)
                searchIsActive = false
            },
            active = searchIsActive,
            onActiveChange = {
                searchIsActive = it
            },
            placeholder = {
                Text(text = "Search Holidays")
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
                            if (holidayQuerySearch.isNotEmpty()) {
                                holidayQuerySearch = ""
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
                    horizontalArrangement = spacedBy(MaterialTheme.spacing.spaceMedium)
                ) {
                    Icon(imageVector = Icons.Filled.History, contentDescription = "search history")
                    Text(text = it)
                }
            }
        }
        if (mainViewModel.holidaysList != null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)) {
                itemsIndexed(filteredHolidayQuery) { index, holidayItem ->
                    AdminHolidaysRow(holiday = holidayItem) { actionItem ->
                        selectedHoliday = actionItem
                        deleteHolidayConfirmDialogShown = true
                    }
                    if (index != lastHolidayItemIndex?.minus(1)) {
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
        if (mainViewModel.isCreateHolidayDialogShown) {
            AdminCreateHolidayDialog(mainViewModel = mainViewModel)
        }
        if (deleteHolidayConfirmDialogShown) {
            AlertDialog(
                onDismissRequest = {
                    deleteHolidayConfirmDialogShown = false
                    selectedHoliday = null
                },
                title = { Text(text = "Delete Holiday") },
                text = { Text(text = "Are you sure you want to delete the holiday: ${formatDateToStringForInputs(selectedHoliday?.date)}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onConfirmDeleteHolidayClicked()
                            deleteHolidayConfirmDialogShown = false
                        }
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            deleteHolidayConfirmDialogShown = false
                            selectedHoliday = null
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
