package com.example.Thesis_Project.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.Thesis_Project.backend.db.db_models.*
import androidx.compose.runtime.getValue
import com.example.Thesis_Project.TimerHelper
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import java.lang.ref.WeakReference

class MainViewModel(val application: Application) : ViewModel() {

    val appContext = application.applicationContext
    var isLoading by mutableStateOf(false)

    val setIsLoading: (Boolean) -> Unit = { newIsLoading ->
        isLoading = newIsLoading
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    //auth
    val auth: FirebaseAuth = Firebase.auth
    val createUserAuth: FirebaseAuth = Firebase.auth

    val db: FirebaseFirestore = Firebase.firestore

    var currentUser: FirebaseUser? by mutableStateOf(null)
    val setCurrentUser: (FirebaseUser?) -> Unit = { newCurrentUser ->
        if (newCurrentUser != null) {
            currentUser = newCurrentUser
            Log.d("set currentUser from firebase", "user: $currentUser")
        } else {
            Log.d("set currentUser from firebase", "user has loggedout")
            currentUser = null
        }
    }

    var userData: User? by mutableStateOf(null)
    val setUserData: (User?) -> Unit = { newUserData ->
        if (newUserData != null) {
            userData = newUserData
            Log.d("set user data", "user: $userData")
        } else {
            Log.d("set user data", "no user found")
            userData = null
        }
    }

    var isLoggedInAsAdmin: Boolean by mutableStateOf(false)
    val setIsLoggedInAsAdmin: (Boolean?) -> Unit = { newIsLoggedInAsAdmin ->
        if (newIsLoggedInAsAdmin != null) {
            isLoggedInAsAdmin = newIsLoggedInAsAdmin
        }
        Log.d("check login as admin", "admin: $isLoggedInAsAdmin")
    }

    var isFaceRegistered: Boolean by mutableStateOf(false)
    val setIsFaceRegistered: (Boolean?) -> Unit = { newIsFaceRegistered ->
        if (newIsFaceRegistered != null) {
            isFaceRegistered = newIsFaceRegistered
        }
        Log.d("check embeddings", "admin: $isFaceRegistered")
    }

    val setUserEmbeddings: (String?) -> Unit = { newEmbeddings ->
        if (newEmbeddings != null) {
            userData?.embedding = newEmbeddings
            Log.d("set user embeddings", "user: ${userData!!.embedding}")
        } else {
            Log.d("set user embeddings", "no user embeddings found")
            userData?.embedding = null
        }
    }

    var isUserAdmin: Boolean by mutableStateOf(false)

    val setUserAdmin: (Boolean?) -> Unit = { isAdmin ->
        if (isAdmin != null) {
            isUserAdmin = isAdmin
        }
        Log.d("check admin", "admin: $isUserAdmin")
    }

    suspend fun signIn(
        email: String,
        password: String,
        onSuccess: suspend () -> Unit,
        onFailure: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val authResult = Tasks.await(auth.signInWithEmailAndPassword(email, password))
                // Authentication successful
                setCurrentUser(authResult.user)
                onSuccess()
            } catch (exception: Exception) {
                Log.e("BRUWIAHIDAW U", "$exception")
                // Handle authentication failure
                if (exception is FirebaseAuthException) {
                    val errorCode = exception.errorCode
                    val errorMessage = exception.message
                    onFailure(errorMessage ?: "Login failed with error code: $errorCode")
                } else {
                    onFailure("Login failed: ${exception.message}")
                }
            }
        }
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    setCurrentUser(auth.currentUser)
//                    if (currentUser != null) {
//                        Log.d(
//                            "currentUser",
//                            "currentUser: ${currentUser}, uid: ${currentUser!!.uid}"
//                        )
//                        onSuccess()
//                    } else {
//                        onFailure("No user detected")
//                    }
//                } else {
//                    val exception = task.exception
//                    if (exception is FirebaseAuthException) {
//                        val errorCode = exception.errorCode
//                        val errorMessage = exception.message
//                        onFailure(errorMessage ?: "Login failed with error code: $errorCode")
//                    } else {
//                        onFailure("Login failed")
//                    }
//                }
//            }
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
        Log.d("isAdminHomeInit", "isAdminHomeInit: $isAdminHomeInit")
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
        Log.d("isAdminUsersInit", "isAdminUsersInit: $isAdminUsersInit")
    }

    var isEditCompanyParamsDialogShown by mutableStateOf(false)
    fun toggleIsEditCompanyParamsDialogShown() {
        isEditCompanyParamsDialogShown = !isEditCompanyParamsDialogShown
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
    val timer = Timer()
    val timerHelper = TimerHelper(appContext)

    var workHourTime by mutableStateOf("0")
    var setWorkHourTime: (String?) -> Unit = { newWorkHourTime ->

        if (newWorkHourTime != null) {
            workHourTime = newWorkHourTime
        }
    }

    fun startTimer(timerHelper: TimerHelper) {
        timerHelper.setTimerCounting(true)
    }
    fun stopTimer(timerHelper: TimerHelper) {
        timerHelper.setTimerCounting(false)
        setWorkHourTime("0")
    }

    fun stopWorkHourTimer(timerHelper: TimerHelper)
    // on tapout
    {
        timerHelper.setStopTime(null)
        timerHelper.setStartTime(null)
        stopTimer(timerHelper)
    }

    fun startWorkHourTimer(timerHelper: TimerHelper) {
        timerHelper.setStartTime(Date())
        startTimer(timerHelper)
    }

    var todayAttendance: Attendance? by mutableStateOf(null)
    var setTodayAttendance: (Attendance?) -> Unit = { newTodayAttendance ->
        if (newTodayAttendance != null) {
            todayAttendance = newTodayAttendance
        }
        Log.d("set today attendance", "today attendance: $todayAttendance")
    }

    var tapInDisabled by mutableStateOf(false)
    var setTapInDisabled: (Boolean?) -> Unit = { newIsTapInDisabled ->
        if (newIsTapInDisabled != null) {
            tapInDisabled = newIsTapInDisabled
        }
        Log.d("tapInDisabled", "tapInDisabled: $tapInDisabled")
    }

    var isTappedIn by mutableStateOf(false)
    val setIsTappedIn: (Boolean?) -> Unit = { newIsTappedIn ->
        if (newIsTappedIn != null) {
            isTappedIn = newIsTappedIn
        }
        Log.d("isTappedIn", "isTappedIn: $isTappedIn")
    }

    var isHomeInit by mutableStateOf(false)
        private set

    val setIsHomeInit: (Boolean?) -> Unit = { newIsHomeInit ->
        if (newIsHomeInit != null) {
            isHomeInit = newIsHomeInit
        }
        Log.d("isHomeInit", "user: $isHomeInit")
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

    //camera
    var isConnectedToSSID: Boolean by mutableStateOf(false)

    val setIsConnectedToSSID: (Boolean?) -> Unit = { newIsConnectedToSSID ->
        if (newIsConnectedToSSID != null) {
            isConnectedToSSID = newIsConnectedToSSID
        }
        Log.d("isConnectedToSSID", "isConnectedToSSID: $isConnectedToSSID")
    }

    var hasTakenPicture: Boolean by mutableStateOf(false)

    val setHasTakenPicture: (Boolean?) -> Unit = { newHasTakenPicture ->
        if (newHasTakenPicture != null) {
            hasTakenPicture = newHasTakenPicture
        }
        Log.d("hasTakenPicture", "hasTakenPicture: $hasTakenPicture")
    }

    //tap in detect
    private val liveProbabilities = mutableStateListOf<Float>()
    private var status = 0
    fun checkLiveness(probLeft: Float, probRight: Float): Int {
        if (liveProbabilities.size < 100) {
            liveProbabilities.add(probLeft)
            liveProbabilities.add(probRight)
            return 0 // Not enough numbers
        }
        val stdTemp = std()
        if (stdTemp >= 0.3f) {
            status = 1
            liveProbabilities.clear()
            return if (status == 0) -1 else 1 // Passed detection after second loop
        } else {
            liveProbabilities.clear()
            return -1 // Failed detection
        }
    }

    fun resetStatus() {
        liveProbabilities.clear()
        status = 0
    }

    private fun std(): Float {
        var total = 0f
        var tempStd = 0f
        for (i in liveProbabilities) {
            total += i
        }
        val mean = total / liveProbabilities.size
        for (i in liveProbabilities) {
            tempStd += (i - mean).pow(2)
        }
        return sqrt(tempStd / liveProbabilities.size)
    }

    //tap in register
    var imgBitmap: Bitmap? by mutableStateOf(null)

    fun setBitmap(bitmap: Bitmap?) {
        imgBitmap = bitmap
    }

    fun clearBitmap() {
        imgBitmap = null
    }

    //history
    var correctionSelected by mutableStateOf(true)
    var leaveSelected by mutableStateOf(false)

    val switchHistoryTab: () -> Unit = {
        correctionSelected = !correctionSelected
        leaveSelected = !leaveSelected
    }

    var isCancelLeaveDialogShown by mutableStateOf(false)

    fun toggleCancelLeaveDialog() {
        isCancelLeaveDialogShown = !isCancelLeaveDialogShown
    }

    var isCancelCorrectionDialogShown by mutableStateOf(false)

    fun toggleCancelCorrectionDialog() {
        isCancelCorrectionDialogShown = !isCancelCorrectionDialogShown
    }

    //calendar
    var isCalendarInit by mutableStateOf(false)
        private set

    val setIsCalendarInit: (Boolean?) -> Unit = { newIsCalendarInit ->
        if (newIsCalendarInit != null) {
            isCalendarInit = newIsCalendarInit
        }
        Log.d("isCalendarInit", "isCalendarInit: $isCalendarInit")
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
            Log.d("calendarSelectedDate", "calendarSelectedDate: $calendarSelectedDate")
        } else {
            Log.d("calendarSelectedDate", "calendarSelectedDate null")
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
        Log.d("isHistoryInit", "isHistoryInit: $isHistoryInit")
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

    suspend fun signOutFromAdmin() {
        setIsAdminHomeInit(false)
        setIsAdminUsersInit(false)
        setIsLoggedInAsAdmin(false)
        isUserAdmin = false
        userData = null
        currentUser = null
        auth.signOut()
    }

    suspend fun signOutFromUser() {
        setIsHomeInit(false)
        setIsCalendarInit(false)
        setIsHistoryInit(false)
        setUserData(null)
        setCompanyVariable(null)
        currentUser = null
        correctionSelected = true
        leaveSelected = false
        auth.signOut()
    }
}