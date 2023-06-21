package com.example.Thesis_Project.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.Thesis_Project.backend.db.db_models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class MainViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)

    val setIsLoading: (Boolean) -> Unit = { newIsLoading ->
        isLoading = newIsLoading
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    //auth
    val auth: FirebaseAuth = Firebase.auth
    val createUserAuth: FirebaseAuth = Firebase.auth

    val db: FirebaseFirestore = Firebase.firestore

    var currentUser: FirebaseUser? by mutableStateOf(null)

    var userData: User? by mutableStateOf(null)

    val setUserData: (User?) -> Unit = { newUserData ->
        if (newUserData != null) {
            userData = newUserData
            Log.d("set user data", "user: $userData")
        }
        else {
            Log.d("set user data", "no user found")
            userData = null
        }
    }

    var isUserAdmin: Boolean by mutableStateOf(false)

    val setUserAdmin: (Boolean?) -> Unit = { isAdmin ->
        if (isAdmin != null) {
            isUserAdmin = isAdmin
        }
        Log.d("check admin", "admin: $isUserAdmin")
    }

    fun checkAuth(): Boolean {
        return currentUser != null
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    if (currentUser != null) {
                        Log.d(
                            "currentUser",
                            "currentUser: ${currentUser}, uid: ${currentUser!!.uid}"
                        )
                        onSuccess()
                    } else {
                        onFailure("No user detected")
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        val errorCode = exception.errorCode
                        val errorMessage = exception.message
                        onFailure(errorMessage ?: "Login failed with error code: $errorCode")
                    } else {
                        onFailure("Login failed")
                    }
                }
            }
    }

    fun changePassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Send change password to email", "changePassword:success")
            } else {
                Log.d(
                    "Send change password to email failed",
                    "changePassword:failure",
                    task.exception
                )
            }
        }
    }

    //admin
    var isAdminHomeInit by mutableStateOf(false)
        private set

    val setIsAdminHomeInit: (Boolean?) -> Unit = { newIsAdminHomeInit ->
        if (newIsAdminHomeInit != null) {
            isAdminHomeInit = newIsAdminHomeInit
        }
        Log.d("get user data", "user: $isAdminHomeInit")
    }

    var usersList: List<User>? by mutableStateOf(null)
    val setUserList: (List<User>?) -> Unit = { newUsers ->
        if (newUsers != null) {
            usersList = newUsers
            Log.d("Get Users list", "Users List: $usersList")
        } else {
            Log.d("Get Users list", "Users List not found")
        }
    }

    var isAdminUsersInit by mutableStateOf(false)
        private set

    val setIsAdminUsersInit: (Boolean?) -> Unit = { newIsAdminUsersInit ->
        if (newIsAdminUsersInit != null) {
            isAdminUsersInit = newIsAdminUsersInit
        }
        Log.d("get user data", "user: $isAdminUsersInit")
    }

    var isEditCompanyParamsDialogShown by mutableStateOf(false)
    fun showEditCompanyParamsDialog() {
        isEditCompanyParamsDialogShown = true
    }

    //adminusers

    var isCreateUserDialogShown by mutableStateOf(false)

    fun toggleCreateUserDialog() {
        isCreateUserDialogShown = !isCreateUserDialogShown
    }

    var isViewUserDialogShown by mutableStateOf(false)

    fun toggleViewUserDialog() {
        isViewUserDialogShown = !isViewUserDialogShown
    }

    //main

    var isHomeInit by mutableStateOf(false)
        private set

    val setIsHomeInit: (Boolean?) -> Unit = { newIsHomeInit ->
        if (newIsHomeInit != null) {
            isHomeInit = newIsHomeInit
        }
        Log.d("get user data", "user: $isHomeInit")
    }

    var companyVariable: CompanyParams? by mutableStateOf(null)

    val setCompanyVariable: (CompanyParams?) -> Unit = { newCompanyParams ->
        if (newCompanyParams != null) {
            companyVariable = newCompanyParams
            Log.d("Set Company Variables", "Company Variables: $companyVariable")
        } else {
            companyVariable = null
            Log.d("Set Company Variables", "Set company variables to null")
        }
    }

    //history
    var correctionSelected by mutableStateOf(true)
    var leaveSelected by mutableStateOf(false)

    val switchHistoryTab: () -> Unit = {
        correctionSelected = !correctionSelected
        leaveSelected = !leaveSelected
    }

    //calendar
    var isCalendarInit by mutableStateOf(false)
        private set

    val setIsCalendarInit: (Boolean?) -> Unit = { newIsCalendarInit ->
        if (newIsCalendarInit != null) {
            isCalendarInit = newIsCalendarInit
        }
        Log.d("get user data", "user: $isCalendarInit")
    }
    var attendanceList: List<Attendance>? by mutableStateOf(null)
    val setAttendanceList: (List<Attendance>?) -> Unit = { newAttendance ->
        if (newAttendance != null) {
            attendanceList = newAttendance
            Log.d("Get attendance list", "Attendance List: $attendanceList")
        } else {
            Log.d("Get Attendance list", "Attendance not found")
        }
    }

    var calendarSelectedDate: LocalDate by mutableStateOf(LocalDate.now())
    val setCalendarSelectedDate: (LocalDate?) -> Unit = { newCalendarSelectedDate ->
        if (newCalendarSelectedDate != null) {
            calendarSelectedDate = newCalendarSelectedDate
            Log.d("Get attendance list", "Attendance List: $calendarSelectedDate")
        } else {
            Log.d("Get Attendance list", "Attendance not found")
        }
    }


    var isRequestLeaveButtonEnabled: Boolean by mutableStateOf(true)
    val setIsRequestLeaveButtonEnabled: (Boolean) -> Unit = { newIsRequestLeaveButtonEnabled ->
        isRequestLeaveButtonEnabled = newIsRequestLeaveButtonEnabled
    }
    var isRequestCorrectionButtonEnabled: Boolean by mutableStateOf(false)
    val setIsRequestCorrectionButtonEnabled: (Boolean) -> Unit = { newIsRequestCorrectionEnabled ->
        isRequestCorrectionButtonEnabled = newIsRequestCorrectionEnabled
    }

    //leave & correction request
    var isHistoryInit by mutableStateOf(false)
        private set

    val setIsHistoryInit: (Boolean?) -> Unit = { newIsHistoryInit ->
        if (newIsHistoryInit != null) {
            isHistoryInit = newIsHistoryInit
        }
        Log.d("get user data", "user: $isHistoryInit")
    }
    var leaveRequestList: List<LeaveRequest>? by mutableStateOf(null)
    var correctionRequestList: List<CorrectionRequest>? by mutableStateOf(null)

    val setLeaveRequestList: (List<LeaveRequest>?) -> Unit = { newLeaveRequest ->
        if (newLeaveRequest != null) {
            leaveRequestList = newLeaveRequest
            Log.d("Get Leave Request list", "Leave Request: $leaveRequestList")
        } else {
            Log.d("Get Leave Request list", "Leave Request not found")
        }
    }
    val setCorrectionRequestList: (List<CorrectionRequest>?) -> Unit = { newCorrectionRequest ->
        if (newCorrectionRequest != null) {
            correctionRequestList = newCorrectionRequest
            Log.d("Get Correction Request list", "Correction Request: $correctionRequestList")
        } else {
            Log.d("Get Correction Request list", "Correction Request not found")
        }
    }

    var isRequestLeaveDialogShown: Boolean by mutableStateOf(false)
    var isCorrectionDialogShown: Boolean by mutableStateOf(false)
    fun toggleRequestLeaveDialog() {
        isRequestLeaveDialogShown = !isRequestLeaveDialogShown
    }
    fun toggleCorrectionDialog() {
        isCorrectionDialogShown = !isCorrectionDialogShown
    }

    fun onRequestLeaveClicked() {
        isRequestLeaveDialogShown = true
    }

    fun onRequestCorrectionClicked() {
        isCorrectionDialogShown = true
    }

    fun signOutFromAdmin() {
        setIsAdminHomeInit(false)
        setIsAdminUsersInit(false)

        isUserAdmin = false
        userData = null
        currentUser = null
        auth.signOut()
    }

    fun signOutFromUser() {
        setIsHomeInit(false)
        setIsCalendarInit(false)
        setIsHistoryInit(false)
        setUserData(null)
        setCompanyVariable(null)

        currentUser = null
        correctionSelected = false
        leaveSelected = false
        auth.signOut()
    }
}