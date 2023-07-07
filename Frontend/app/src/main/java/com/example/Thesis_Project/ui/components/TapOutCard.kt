package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.checkDateIsHoliday
import com.example.Thesis_Project.ui.utils.checkDateIsWeekend
import com.example.Thesis_Project.ui.utils.timeStringFromLong
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun TapOutCard(navController: NavController, mainViewModel: MainViewModel) {

    val tapOutScope = rememberCoroutineScope()

    val context: Context = LocalContext.current
    var tapOutConfirmDialogShown by rememberSaveable { mutableStateOf(false) }
    var workTimeLeft: Long? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        while (true) {
            if (mainViewModel.timerHelper.timerCounting()) {
                workTimeLeft =
                    (mainViewModel.companyVariable?.companyworktime?.times(60000L))?.minus(
                        (Date().time - mainViewModel.timerHelper.startTime()!!.time)
                    )
            } else {
                break
            }
            delay(1000L)
        }
    }

    val postTimeOut: suspend (attendance: Attendance) -> Unit = { attendance ->
        mainViewModel.setIsLoading(true)
        try {
            db_util.tapOutAttendance(
                mainViewModel.db,
                mainViewModel.userData!!,
                attendance,
                mainViewModel.companyVariable!!
            )
            db_util.getAttendance(
                mainViewModel.db,
                mainViewModel.userData!!.userid,
                db_util.firstDateOfMonth(),
                db_util.lastDateOfMonth(),
            ) { attendances ->
                if (attendances == null) {
                    mainViewModel.setAttendanceList(null)
                } else {
                    mainViewModel.setAttendanceList(attendances)
                }
            }
            mainViewModel.stopWorkHourTimer(mainViewModel.timerHelper)
            mainViewModel.showToast(context, "Tapped out successfully")
            navController.popBackStack()
            navController.navigate(NavGraphs.HOME)
            mainViewModel.setTapInDisabled(true)
            mainViewModel.setIsTappedIn(false)
        } catch (e: Exception) {
            Log.e("Error", "Failed to tap out: $e")
        }
        mainViewModel.setIsLoading(false)
    }

    fun onTapOut() {
        tapOutConfirmDialogShown = true
    }

    fun onConfirmTapOut() {
        val attendance = Attendance(
            attendanceid = mainViewModel.todayAttendance?.attendanceid,
            userid = mainViewModel.todayAttendance?.userid,
            timein = mainViewModel.todayAttendance?.timein
        )
        tapOutScope.launch {
            postTimeOut(attendance)
        }
    }

    if (tapOutConfirmDialogShown) {
        AlertDialog(
            onDismissRequest = {
                tapOutConfirmDialogShown = false
            },
            title = { Text(text = "Tap Out") },
            text = {
                if (workTimeLeft!! <= 0){
                    Text(
                        text = "Are you sure you want to tap out?"
                    )
                }
                else {
                    Text(
                        text = "Are you sure you want to tap out?\nYou have ${
                            timeStringFromLong(
                                workTimeLeft ?: 0
                            )
                        } left"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmTapOut()
                        tapOutConfirmDialogShown = false
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        tapOutConfirmDialogShown = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = colorResource(
                id = R.color.white
            )
        ), elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.spaceLarge,
                alignment = Alignment.CenterVertically
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceSmall,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hours worked",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = mainViewModel.workHourTime,
                style = MaterialTheme.typography.headlineSmall, letterSpacing = 1.sp,
                color = colorResource(id = R.color.blue_500)
            )
            ButtonMaxWidth(onClickCallback = { onTapOut() }, buttonText = "Tap Out")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        TapOutCard()
//    }
//}