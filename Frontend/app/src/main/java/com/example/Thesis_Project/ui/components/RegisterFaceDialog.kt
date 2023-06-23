package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.SensorOccupied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.formatDateToString
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterFaceDialog(mainViewModel: MainViewModel, navController: NavController) {

    fun onTakePhotoClicked(){
        navController.navigate(NavGraphs.REGISTERFACE)
    }

    Dialog(
        onDismissRequest = { onTakePhotoClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {

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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SensorOccupied,
                    contentDescription = null,
                    tint = colorResource(
                        id = R.color.blue_500
                    ),
                    modifier = Modifier
                        .size(MaterialTheme.spacing.iconLarge)
                        .clickable { navController.popBackStack() },
                )
                Text(text = "Please register your face once to tap in")
                ButtonHalfWidth(onClick = {onTakePhotoClicked()}, buttonText = "Take a photo")
            }
        }
    }
}