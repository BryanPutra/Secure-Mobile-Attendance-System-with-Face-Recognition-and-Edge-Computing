package com.example.Thesis_Project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.ui.navgraphs.RootNavigationGraph
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.viewmodel.AdminViewModel
import com.example.Thesis_Project.viewmodel.AdminViewModelFactory
import com.example.Thesis_Project.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val adminViewModel: AdminViewModel by viewModels<AdminViewModel>()
    private val adminViewModelFactory = AdminViewModelFactory(mainViewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        adminViewModel = ViewModelProvider(this, adminViewModelFactory)[AdminViewModel::class.java]
        setContent {
            SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
                RootNavigationGraph(
                    navController = rememberNavController(),
                    mainViewModel = mainViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}