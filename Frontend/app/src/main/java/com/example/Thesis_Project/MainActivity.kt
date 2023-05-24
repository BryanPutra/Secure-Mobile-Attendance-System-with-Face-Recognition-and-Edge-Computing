package com.example.Thesis_Project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ui.navgraphs.RootNavigationGraph
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }
}