package com.example.Thesis_Project.ui.screens.camera

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.concurrent.Executors

@Composable
fun TapInScreen(navController: NavController, mainViewModel: MainViewModel) {
    TapInContainer(navController, mainViewModel)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TapInContainer(navController: NavController, mainViewModel: MainViewModel) {
    val permissions = if (Build.VERSION.SDK_INT <= 28) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    } else listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE
    )
    // Create a permission request state for camera
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    // Check if the permission is granted
    if (cameraPermissionState.allPermissionsGranted) {
        // Permission granted, show camera preview or perform camera operations
        Text(text = "Camera Permission Granted!")
    } else {
        // Request the permission
        LaunchedEffect(cameraPermissionState) {
            cameraPermissionState.launchMultiplePermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.allPermissionsGranted) {
            TapInCameraView(
                executor = Executors.newSingleThreadExecutor(),
                onError = { exception -> Log.e("Error scanning photo", "$exception") },
                mainViewModel = mainViewModel, navController = navController
            )
        }
    }
    // we will show camera preview once permission is granted
}