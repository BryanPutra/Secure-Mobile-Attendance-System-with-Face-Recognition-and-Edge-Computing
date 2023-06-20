package com.example.Thesis_Project.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Thesis_Project.backend.db.db_models.*
import com.example.Thesis_Project.backend.db.db_util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {

    //auth
    val auth: FirebaseAuth = Firebase.auth
    val createUserAuth: FirebaseAuth = Firebase.auth

    val db: FirebaseFirestore = Firebase.firestore

    var currentUser: FirebaseUser? by mutableStateOf(null)

    var userData: User? by mutableStateOf(null)

    val setUserData: (User?) -> Unit = { newUserData ->
        if (newUserData != null) {
            userData = newUserData
        }
        Log.d("get user data", "user: $userData")
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

//    suspend fun createUserFromAdmin(
//        createUserAuth: FirebaseAuth,
//        db: FirebaseFirestore,
//        user: User,
//        email: String,
//        password: String,
//        companyparams: CompanyParams
//    ) {
//        viewModelScope.launch{
//            db_util
//        }
//    }

    var isEditCompanyParamsDialogShown by mutableStateOf(false)
    fun showEditCompanyParamsDialog() {
        isEditCompanyParamsDialogShown = true
    }

    //adminusers
//    var filteredUsersList: List<User>? by mutableStateOf(null)
//    val setFilteredUsersList: (List<User>?) -> Unit = { newFilteredUsersList ->
//        if (newFilteredUsersList != null) {
//            filteredUsersList = newFilteredUsersList
//            Log.d("Change Users list", "Users List: $filteredUsersList")
//        }
//        else{
//            Log.d("Change Users list", "Users List not found")
//        }
//    }

    var isCreateUserDialogShown by mutableStateOf(false)

    fun toggleCreateUserDialog() {
        isCreateUserDialogShown = !isCreateUserDialogShown
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
            Log.d("Get Company variables", "Company Variables: $companyVariable")
        } else {
            Log.d("Get Company variables", "Company Variables not found")
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
    var calendarSelectedDate: LocalDate by mutableStateOf(LocalDate.now())

    val setAttendanceList: (List<Attendance>?) -> Unit = { newAttendance ->
        if (newAttendance != null) {
            attendanceList = newAttendance
            Log.d("Get attendance list", "Attendance List: $attendanceList")
        } else {
            Log.d("Get Attendance list", "Attendance not found")
        }
    }

    var isRequestLeaveButtonEnabled: Boolean by mutableStateOf(true)
    var isRequestCorrectionButtonEnabled: Boolean by mutableStateOf(true)

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

    var isRequestLeaveDialogShown: Boolean by mutableStateOf(false)
    var isCorrectionLeaveDialogShown: Boolean by mutableStateOf(false)

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

    fun onRequestLeaveClicked() {
        isRequestLeaveDialogShown = true
    }

    fun onRequestCorrectionClicked() {
        isCorrectionLeaveDialogShown = true
    }

    fun signOutFromAdmin() {
        isUserAdmin = false
        userData = null
        currentUser = null
        auth.signOut()
    }

    fun signOutFromUser() {
        currentUser = null
        userData = null
        companyVariable = null
        correctionSelected = false
        leaveSelected = false
        auth.signOut()
    }
}