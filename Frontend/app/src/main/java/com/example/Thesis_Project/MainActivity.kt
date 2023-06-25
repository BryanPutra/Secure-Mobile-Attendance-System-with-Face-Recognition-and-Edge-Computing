package com.example.Thesis_Project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ui.navgraphs.RootNavigationGraph
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.ui.utils.timeStringFromLong
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.example.Thesis_Project.viewmodel.MainViewModelFactory
import java.util.*

class MainActivity : ComponentActivity() {
    val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        class TimeTask() : TimerTask() {
            override fun run() {
                if (mainViewModel.timerHelper.timerCounting()) {
                    val time = Date().time - mainViewModel.timerHelper.startTime()!!.time
                    mainViewModel.setWorkHourTime(timeStringFromLong(time))
                }
            }
        }

        if (mainViewModel.timerHelper.timerCounting()) {
            mainViewModel.startTimer(mainViewModel.timerHelper)
        } else {
            mainViewModel.stopTimer(mainViewModel.timerHelper)
        }

        mainViewModel.timer.scheduleAtFixedRate(TimeTask(), 1000L, 1000L)

        setContent {
            SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
                RootNavigationGraph(
                    navController = rememberNavController(),
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}