package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Holiday
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

@Composable
fun AdminCreateHolidayDialog(mainViewModel: MainViewModel) {

    val createHolidayScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var name by rememberSaveable { mutableStateOf("") }
    var dateIsValid by remember { mutableStateOf(true) }
    var nameIsValid by remember { mutableStateOf(true) }

    var errorText by remember { mutableStateOf("") }

    val calendarHolidayDateState = rememberUseCaseState()

    val postCreateHoliday: suspend (holiday: Holiday) -> Unit = { holiday ->
        mainViewModel.setIsLoading(true)
        try {
            db_util.addHolidayManual(
                mainViewModel.db,
                holiday,
            )
            db_util.getHolidays(mainViewModel.db, null, mainViewModel.setHolidayList)
            errorText = ""
            mainViewModel.showToast(context, "Holiday has been created successfully")
            mainViewModel.toggleCreateHolidayDialog()
        } catch (e: Exception) {
            errorText = "Failed to create holiday: ${e.message}"
            Log.e("Error", "Failed to create holiday: $e")
        }
        mainViewModel.setIsLoading(false)
    }

    fun onSubmitClicked() {
        dateIsValid =
            !(mainViewModel.holidaysList?.any { it.date == db_util.localDateToDate(date) } ?: false)
        nameIsValid = isValidName(name)

        if (!dateIsValid) {
            errorText = "Date already exists"
            return
        }

        if (!nameIsValid) {
            errorText = "Name needs to be at least 3 characters"
            return
        }

        val holiday = Holiday(
            date = db_util.localDateToDate(date),
            holidayname = name
        )
        createHolidayScope.launch {
            postCreateHoliday(holiday)
        }
    }

    fun onCancelClicked() {
        mainViewModel.toggleCreateHolidayDialog()
    }

    CalendarDialog(
        state = calendarHolidayDateState,
        config = CalendarConfig(
            monthSelection = true,
        ),
        selection = CalendarSelection.Date { newDate -> date = newDate }
    )

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
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
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Text(
                    text = "Create Holiday",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f)
                                .clickable {
                                    calendarHolidayDateState.show()
                                },
                            value = formatLocalDateToString(date),
                            onValueChange = {},
                            isError = !dateIsValid,
                            readOnly = true,
                            enabled = false,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = colorResource(id = R.color.black),
                                disabledTextColor = colorResource(id = R.color.black),
                                disabledLabelColor = colorResource(id = R.color.black)
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = colorResource(id = R.color.blue_500)
                                )
                            })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Name",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f),
                            value = name,
                            onValueChange = { newName -> name = newName },
                            isError = !nameIsValid,
                        )
                    }
                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
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
                            onClick = {
                                onSubmitClicked()
                            },
                            buttonText = "Submit"
                        )
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Cancel")
                    }
                }
            }
        }
    }
}

