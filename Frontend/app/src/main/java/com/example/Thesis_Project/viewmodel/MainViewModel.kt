package com.example.Thesis_Project.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CompanyParams
import com.example.Thesis_Project.backend.db.db_models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class MainViewModel: ViewModel() {
    //main
    val db: FirebaseFirestore = Firebase.firestore
    var userData: User? by mutableStateOf(null)
    var companyVariable: CompanyParams? by mutableStateOf(null)

    val setCompanyVariable: (CompanyParams?) -> Unit = { newCompanyParams ->
        if (newCompanyParams != null){
            companyVariable = newCompanyParams
        }
        Log.d("Get Company variables","Company Variables: $companyVariable")
    }

    //history
    var correctionSelected by mutableStateOf(true)
    var leaveSelected by  mutableStateOf(false)

    val switchHistoryTab: () -> Unit = {
        correctionSelected = !correctionSelected
        leaveSelected = !leaveSelected
    }

    //calendar
    var attendanceList: List<Attendance>? by mutableStateOf(null)
    var calendarSelectedDate: LocalDate by mutableStateOf(LocalDate.now())

    val setAttendanceList: (List<Attendance>?) -> Unit = { newAttendance ->
        if (newAttendance != null){
            attendanceList = newAttendance
        }
        Log.d("Get attendance list","Company Variables: $attendanceList")
    }
}