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
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.TimerHelper
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun TapOutCard(navController: NavController, mainViewModel: MainViewModel) {

    val tapOutScope = rememberCoroutineScope()

    val context: Context = LocalContext.current
    val timerHelper = TimerHelper(context)

    val postTimeOut: suspend (attendance: Attendance) -> Unit = { attendance ->
        mainViewModel.setIsLoading(true)
        try {
            db_util.tapOutAttendance(mainViewModel.db, mainViewModel.userData!!, attendance, mainViewModel.companyVariable!!)
            mainViewModel.stopWorkHourTimer(timerHelper)
            mainViewModel.showToast(context, "Tapped out successfully")
            navController.popBackStack()
            navController.navigate(NavGraphs.HOME)
            mainViewModel.setTapInDisabled(false)
            mainViewModel.setIsTappedIn(false)
        } catch (e: Exception) {
            Log.e("Error", "Failed to tap out: $e")
        }
        mainViewModel.setIsLoading(false)
    }

    fun onTapOut() {
        val attendance = Attendance(
            attendanceid = mainViewModel.todayAttendance?.attendanceid,
            userid = mainViewModel.todayAttendance?.userid,
            )
        tapOutScope.launch {
            postTimeOut(attendance)
        }
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
                Text(text = "Work hours left", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(
                text = mainViewModel.workHourTime,
                style = MaterialTheme.typography.headlineLarge,
                color = colorResource(id = R.color.blue_500)
            )
            ButtonMaxWidth(onClickCallback = { onTapOut() }, buttonText = "Tap In")
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