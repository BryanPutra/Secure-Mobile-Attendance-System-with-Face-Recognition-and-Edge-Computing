package com.example.Thesis_Project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.Thesis_Project.ui.Navigation
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
                // A surface container using the 'background' color from the theme
                Navigation()
            }
        }
    }
}