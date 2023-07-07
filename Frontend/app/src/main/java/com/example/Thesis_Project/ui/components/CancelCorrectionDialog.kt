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
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToString
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.ui.utils.formatDateToStringTimeOnly
import com.example.Thesis_Project.ui.utils.isAttended
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CancelCorrectionDialog(
    correctionRequest: CorrectionRequest?,
    mainViewModel: MainViewModel,
    onCancelClicked: () -> Unit
) {

    LaunchedEffect(Unit){
        runBlocking {
            db_util.getAttendanceById(mainViewModel.db, correctionRequest?.attendanceid!!, mainViewModel.setSelectedCorrectionRequestAttendance)
        }
    }

    val cancelCorrectionScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var cancelRequestConfirmDialogShown by rememberSaveable { mutableStateOf(false) }

    val postCancelCorrectionRequest: suspend (correctionRequest: CorrectionRequest) -> Unit =
        { correctionRequestItem ->
            mainViewModel.setIsLoading(true)
            try {
                db_util.cancelCorrectionRequest(
                    mainViewModel.db,
                    correctionRequestItem.correctionrequestid!!
                ) { cancelCorrectionSuccess ->
                    if (cancelCorrectionSuccess) {
                        db_util.getCorrectionRequest(
                            mainViewModel.db,
                            mainViewModel.userData?.userid,
                            mainViewModel.setCorrectionRequestList
                        )
                        db_util.getUser(
                            mainViewModel.db,
                            mainViewModel.userData?.userid!!,
                            mainViewModel.setUserData
                        )
                        mainViewModel.showToast(
                            context,
                            "Correction Request cancelled successfully"
                        )
                        mainViewModel.toggleCancelCorrectionDialog()
                    } else {
                        mainViewModel.showToast(
                            context,
                            "Failed to cancel correction request"
                        )
                        mainViewModel.toggleCancelCorrectionDialog()
                        Log.e("Error", "Failed to cancel correction request")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(
                    context,
                    "Failed to cancel correction request: $e"
                )
                mainViewModel.toggleCancelCorrectionDialog()
                Log.e("Error", "Failed to cancel correction request: $e")
            }
            mainViewModel.setIsLoading(false)
        }

    fun onCancelRequestClicked() {
        cancelCorrectionScope.launch {
            if (correctionRequest != null) {
                postCancelCorrectionRequest(correctionRequest)
            }
        }
    }
    if (cancelRequestConfirmDialogShown) {
        AlertDialog(
            onDismissRequest = { cancelRequestConfirmDialogShown = false },
            title = { Text(text = "Cancel Correction Request") },
            text = { Text(text = "Are you sure you want to cancel the correction request?") },
            confirmButton = {
                Button(
                    onClick = {
                        cancelRequestConfirmDialogShown = false
                        onCancelRequestClicked()
                    }
                ) {
                    Text(text = "Cancel Correction")
                }
            },
            dismissButton = {
                Button(
                    onClick = { cancelRequestConfirmDialogShown = false }
                ) {
                    Text(text = "Close")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
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
                    }
                    else {
                        if (mainViewModel.selectedCorrectionRequestAttendance != null) {
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
                                        mainViewModel.selectedCorrectionRequestAttendance?.permissionflag == true -> "Change permission date"
                                        mainViewModel.selectedCorrectionRequestAttendance?.leaveflag == true -> "Change leave date"
                                        isAttended(mainViewModel.selectedCorrectionRequestAttendance) -> "Change attendance time"
                                        else -> " "
                                    },
                                    modifier = Modifier.weight(2f),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left
                                )
                            }
                        }
                        if (mainViewModel.selectedCorrectionRequestAttendance?.permissionflag == true || mainViewModel.selectedCorrectionRequestAttendance?.leaveflag == true){
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
                                    text = formatDateToStringForInputs(mainViewModel.selectedCorrectionRequestAttendance?.timein) ?: "",
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
                        }
                        else {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                ) {
                    if (correctionRequest?.approvedflag == true || correctionRequest?.rejectedflag == true) {
                        Box(modifier = Modifier.weight(1f)) {
                            ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Close")
                        }
                    }
                    else {
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Close")
                        }
                        Box(modifier = Modifier.weight(0.5f)) {
                            ButtonHalfWidth(
                                onClick = {
                                    cancelRequestConfirmDialogShown = true
                                },
                                buttonText = "Cancel Request"
                            )
                        }
                    }
                }
            }
        }
    }
}