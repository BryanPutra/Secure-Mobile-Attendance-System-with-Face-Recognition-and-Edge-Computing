package com.example.Thesis_Project.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.Thesis_Project.backend.db.db_models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class MainViewModel : ViewModel() {
    //main
    val db: FirebaseFirestore = Firebase.firestore
    var userData: User? by mutableStateOf(null)
    var companyVariable: CompanyParams? by mutableStateOf(null)

    val setCompanyVariable: (CompanyParams?) -> Unit = { newCompanyParams ->
        if (newCompanyParams != null) {
            companyVariable = newCompanyParams
        }
        Log.d("Get Company variables", "Company Variables: $companyVariable")
    }

    //history
    var correctionSelected by mutableStateOf(true)
    var leaveSelected by mutableStateOf(false)

    val switchHistoryTab: () -> Unit = {
        correctionSelected = !correctionSelected
        leaveSelected = !leaveSelected
    }

    //calendar
    var attendanceList: List<Attendance>? by mutableStateOf(null)
    var calendarSelectedDate: LocalDate by mutableStateOf(LocalDate.now())

    val setAttendanceList: (List<Attendance>?) -> Unit = { newAttendance ->
        if (newAttendance != null) {
            attendanceList = newAttendance
        }
        Log.d("Get attendance list", "Attendance List: $attendanceList")
    }

    var isRequestLeaveButtonEnabled: Boolean by mutableStateOf(true)
    var isRequestCorrectionButtonEnabled: Boolean by mutableStateOf(true)

    //leave & correction request
    var leaveRequestList: List<LeaveRequest>? by mutableStateOf(null)
    var correctionRequestList: List<CorrectionRequest>? by mutableStateOf(null)

    var isRequestLeaveDialogShown: Boolean by mutableStateOf(false)
    var isCorrectionLeaveDialogShown: Boolean by mutableStateOf(false)

    val setLeaveRequestList: (List<LeaveRequest>?) -> Unit = { newLeaveRequest ->
        if (newLeaveRequest != null) {
            leaveRequestList = newLeaveRequest
        }
        Log.d("Get Leave Request list", "Leave Request: $leaveRequestList")
    }
    val setCorrectionRequestList: (List<CorrectionRequest>?) -> Unit = { newCorrectionRequest ->
        if (newCorrectionRequest != null) {
            correctionRequestList = newCorrectionRequest
        }
        Log.d("Get Correction Request list", "Correction Request: $correctionRequestList")
    }
    fun onRequestLeaveClicked() {
        isRequestLeaveDialogShown = true
    }

    fun onRequestCorrectionClicked() {
        isCorrectionLeaveDialogShown = true
    }

}