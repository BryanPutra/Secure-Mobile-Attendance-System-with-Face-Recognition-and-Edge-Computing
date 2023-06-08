package com.example.Thesis_Project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ui.navgraphs.RootNavigationGraph
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
                RootNavigationGraph(navController = rememberNavController(), mainViewModel = mainViewModel)
            }
        }
    }
}