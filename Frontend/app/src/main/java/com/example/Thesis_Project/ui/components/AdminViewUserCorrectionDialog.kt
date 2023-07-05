package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToString
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.ui.utils.formatDateToStringTimeOnly
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun AdminViewUserCorrectionDialog(
    correctionRequest: CorrectionRequest?,
    mainViewModel: MainViewModel,
    onCloseClicked: () -> Unit
) {

    var selectedCorrectionRequestAttendance: Attendance? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        runBlocking {
            mainViewModel.setIsLoading(true)
            db_util.getAttendanceById(
                mainViewModel.db,
                correctionRequest?.attendanceid!!
            ) { newCorrectionRequestAttendance ->
                selectedCorrectionRequestAttendance = newCorrectionRequestAttendance
            }
            mainViewModel.setIsLoading(false)
        }
    }

    if (mainViewModel.isLoading){
        CircularLoadingBar()
    }

    Dialog(
        onDismissRequest = { onCloseClicked() },
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
            if (selectedCorrectionRequestAttendance != null) {
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
                        text = "Correction",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (correctionRequest?.leaveflag == true || correctionRequest?.permissionflag == true || correctionRequest?.presentflag == true) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            ) {
                                Text(
                                    text = "Correction",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = when {
                                        correctionRequest.leaveflag == true -> "Absent to Leave"
                                        correctionRequest.permissionflag == true -> "Absent to Permission"
                                        else -> "Absent to Present"
                                    },
                                    modifier = Modifier.weight(2f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            ) {
                                Text(
                                    text = "Date",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = formatDateToStringForInputs(correctionRequest.timein) ?: "",
                                    modifier = Modifier.weight(2f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left
                                )
                            }
                            if (correctionRequest.presentflag == true) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "Tap In",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringTimeOnly(correctionRequest.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "Tap Out",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringTimeOnly(correctionRequest.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                            ) {
                                Text(
                                    text = "Correction",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = when {
                                        selectedCorrectionRequestAttendance?.permissionflag == true -> "Change permission date"
                                        selectedCorrectionRequestAttendance?.leaveflag == true -> "Change leave date"
                                        else -> "Change attendance time"
                                    },
                                    modifier = Modifier.weight(2f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left
                                )
                            }
                            if (selectedCorrectionRequestAttendance?.permissionflag == true || selectedCorrectionRequestAttendance?.leaveflag == true) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "From",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringForInputs(selectedCorrectionRequestAttendance?.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "To",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringForInputs(correctionRequest?.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "Tap In",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringTimeOnly(correctionRequest?.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                                ) {
                                    Text(
                                        text = "Tap Out",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = formatDateToStringTimeOnly(correctionRequest?.timein) ?: "",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            }
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        ) {
                            Text(
                                text = "Reason",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = correctionRequest?.reason ?: "",
                                modifier = Modifier.weight(2f),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        ) {
                            Text(
                                text = "Created At",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = formatDateToString(correctionRequest?.createdate) ?: "",
                                modifier = Modifier.weight(2f),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left
                            )
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ButtonHalfWidth(onClick = { onCloseClicked() }, buttonText = "Close")
                    }
                }
            }
        }
    }
}