package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


object db_testing {

    fun runTests(db: FirebaseFirestore,user: User){
//        db_util.getAllUser(db){users ->
//            if(users != null){
//                for(i in users){
//                    Log.e("TESTING",i.userid!!)
//                }
//            }
//        }
//        // For get, if userid == null, will get for all users
//        testGetAttendance(db,userid);
//        testGetLeaveRequest(db,userid);
//        testGetCorrectionRequest(db,userid);
//
//        testCreateAttendance(db,user);
//        testCreateUser(db,UUID.randomUUID().toString(),"good2@gmail.com",false,"Aric Hernando")

//        val holidaydates = mutableListOf<Date>()
//        holidaydates.add(Date.from(LocalDate.of(2022,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
//        holidaydates.add(Date.from(LocalDate.of(2022,12,25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
//        db_util.getCompanyParams(db){params ->
//            db_util.checkYearlyMaintenanceDone(db){ result ->
//                if(!result!!){
//                    db_util.adminYearlyMaintenance(db,params!!, holidaydates)
//                } else {
//                    // Popup yearly maintenance already done by other admin
//                    Log.e("TESTING","ALREADY DONE")
//                }
//            }
//        }
//        db_util.deleteHolidayManual(db,"hgMirIxqZZ6cCPvDZsnc")

//        testCreateLeaveRequest(db,user)

//        db_util.getAttendance(db,user.userid,db_util.startOfDay(LocalDate.now().minusDays(0)),db_util.endOfDay(LocalDate.now().minusDays(0))){ data ->
//            if(data!= null && data.isNotEmpty()){
//                testCreateCorrectionRequest(db,user,data[0])
//            }
//        }

//        db_util.getCorrectionRequest(db,user.userid){ req ->
//            if(req!!.isNotEmpty()){
//                db_util.getCompanyParams(db){data ->
//                    testApproveCorrectionRequest(db,req[0],user,data!!)
//                }
//            }
//        }

        // 14 days check back run only once when user enter home/login, don't run again if user go back to home tab
//        db_util.getCompanyParams(db){params ->
//            db_util.checkBackAttendance(db,user,params!!){ popupflag ->
//                if(popupflag != null){
//                    Log.d("Testing","Success!")
//                }
//            }
//        }

//        testCancelCorrectionRequest(db,"58l1N7R3wFQcITEPQ3t0")
//        db_util.addHolidayManual(db,Date.from(LocalDate.now().atStartOfDay().plusDays(5).atZone(ZoneId.systemDefault()).toInstant()))
//        testRejectCorrectionRequest(db,"nDG36ZOgWGVvaNVeeqQG",userid);
//        testRejectLeaveRequest(db,"gFgsZdwLeQQA45X9bRtV",userid);

    }
//    fun testGetAttendance(db: FirebaseFirestore, userid: String? = null){
//        val datestart = db_util.firstDateOfMonth();
//        val dateend = db_util.lastDateOfMonth();
//
//        db_util.getAttendance(db, userid,datestart,dateend) {data ->
//            if(data != null){
//                if(data.isNotEmpty()) {
//                    for (i in data) {
//                        Log.d("ATTENDANCEDATA", i.timein!!.toString())
//                    }
//                }
//                else{
//                    Log.e("ATTENDANCEDATA", "Attendance not found") // Error because there is no data in db
//                }
//            } else {
//                Log.e("ATTENDANCEDATA", "Attendance not found")  // Error because firestore fetch failed
//            }
//        }
//    }

//    fun testCreateUser(db:FirebaseFirestore, uid: String, email: String, adminflag: Boolean, name: String){
//        // All other user info is handled in backend
//        val usertemp = User(
//            userid = uid,
//            email = email,
//            adminflag = adminflag,
//            name = name,
//                           )
//        db_util.getCompanyParams(db){data ->
//            db_util.createUser(db,usertemp, data!!)
//        }
//    }

//    fun testCheckLoginUserAdmin(db: FirebaseFirestore, userid: String){
//        db_util.checkUserIsAdmin(db, userid) { result ->
//            if (result != null) {
//                if (result) {
//                    // User is admin, success login
//                    // do stuff
//                } else {
//                    // User is not admin, don't login
//                    // do stuff
//                }
//            }
//        }
//    }


//    fun testGetLeaveRequest(db:FirebaseFirestore, userid: String? = null){
//        db_util.getLeaveRequest(db, userid) {data ->
//            if(data != null){
//                if(data.isNotEmpty()) {
//                    for (i in data) {
//                        Log.d("LEAVEREQDATA", i.createdate!!.toString())
//                    }
//                }
//                else{
//                    Log.e("LEAVEREQDATA", "Leave request not found") // Error because there is no data in db
//                }
//            } else {
//                Log.e("LEAVEREQDATA", "Leave request not found")  // Error because firestore fetch failed
//            }
//        }
//    }

//    fun testGetCorrectionRequest(db: FirebaseFirestore, userid: String?){
//        db_util.getCorrectionRequest(db, userid) {data ->
//            if(data != null){
//                if(data.isNotEmpty()) {
//                    for (i in data) {
//                        Log.d("CORRECTREQDATA", i.createdate!!.toString())
//                    }
//                }
//                else{
//                    Log.e("CORRECTREQDATA", "Correction request not found") // Error because there is no data in db
//                }
//            } else {
//                Log.e("CORRECTREQDATA", "Correction request not found")  // Error because firestore fetch failed
//            }
//        }
//    }

    // Logic for tap in
    fun testCreateAttendance(db: FirebaseFirestore, user: User){
        db_util.checkTapOutStatus(db, user.userid!!) { status ->
            // Maybe move checkTapOutStatus function to frontend when view is generated
            if (!status!!) {
                // Face recognition stuff here
                // If pass create attendance
                val temp_attendance = Attendance(
                    userid = user.userid,
                    leaveflag = false,
                    permissionflag = false,
                    absentflag = false,
                    timein = db_util.curDateTime()
                )
                db_util.createAttendance(db, temp_attendance, user);
            }
        }
    }

//     Logic for tapout
//    fun testTapOutAttendance(db:FirebaseFirestore, user: User){
//        db_util.getAttendance(db, user.userid!!,db_util.startOfDay(LocalDate.now()),db_util.endOfDay(LocalDate.now())) { data ->
//            if (data != null) {
//                // Checks whether user have attendance or not today
//                if(data.isNotEmpty()){
//                    // Later don't need to get company params anymore later because it is stored locally
//                    db_util.getCompanyParams(db) { params ->
//                        db_util.tapOutAttendance(db, user, data[0], params!!)
//                    }
//                }
//            }
//        }
//    }

//    fun testCreateLeaveRequest(db: FirebaseFirestore,user: User){
//        // Frontend should check whether user leaveallow = true or not here
//        // Make sure they can only choose dates > today and only this month
//        val temp_leave = LeaveRequest(
//            userid=user.userid,
//            leavestart=db_util.firstDateOfMonth(),
//            leaveend=Date.from(db_util.dateToLocalDate(db_util.firstDateOfMonth()).plusDays(2).atStartOfDay().atZone(
//                ZoneId.systemDefault()).toInstant()),
//            permissionflag = false,
//            reason="testing");
//        // Need to calculate duration of leaverequest after declaring, alternatively can calculate from frontend before creating LeaveRequest object (up to you)
//        temp_leave.duration = db_util.calcDurationDays(temp_leave.leavestart!!,temp_leave.leaveend!!)
//        db_util.checkValidLeaveRequestDate(db,user.userid!!,temp_leave.leavestart,temp_leave.duration!!){valid->
//            if(valid != null){
//                if(valid == false){
//                    // Popup here
//                    Log.e("CREATELEAVEREQUEST","Selected leave request range overlaps with an existing attendance/request")
//                }
//                else{
//                    db_util.checkPendingRequestDuration(db,user.userid!!,temp_leave.leavestart){leaveamt, permamt->
//                        if(leaveamt != null){
//                            // Frontend should probably get company params and save it to ViewModel or somewhere so don't have to keep calling getCompanyParams
//                            db_util.getCompanyParams(db){companyParams->
//                                if(companyParams != null) {
//                                    if (temp_leave.permissionflag!!) {
//                                        db_util.getTotalPermissionThisYear(db, user.userid) { data ->
//                                            if (data != null) {
//                                                if (temp_leave.duration!! + data + permamt!! > companyParams.maxpermissionsleft!!) {
//                                                    // Put popup permissions left not enough, can create but will deduct leave left
//                                                    // If leaveleft not enough will count as absent
//                                                    // If user agrees run createLeaveRequest
//                                                    db_util.createLeaveRequest(db, temp_leave)
//
//                                                    // If user disagrees close popup and do nothing
//                                                } else {
//                                                    db_util.createLeaveRequest(db, temp_leave)
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        db_util.getTotalLeaveThisMonth(db, user.userid) { data ->
//                                            if (data != null) {
//                                                if (temp_leave.duration!! + data + leaveamt!! > companyParams.maxmonthlyleaveleft!!) {
//                                                    // Put error popup here on frontend
//                                                    Log.e(
//                                                        "CREATELEAVEREQUEST",
//                                                        "Leave request exceeds monthly quota"
//                                                         )
//                                                } else if (temp_leave.duration!! + leaveamt!! > user.leaveleft!!) {
//                                                    // Put error popup here on frontend
//                                                    Log.e(
//                                                        "CREATELEAVEREQUEST",
//                                                        "Not enough leave left to create request"
//                                                         )
//                                                } else {
//                                                    db_util.createLeaveRequest(db, temp_leave)
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    // attendance is the date they choose for request
//    fun testCreateCorrectionRequest(db: FirebaseFirestore,user: User, attendance:Attendance) {
//        // Guideline for creating corretion request for all scenarios:
//        // timein and timeout stores both date and time of every entry
//        // date parameter in companyTimeIn and companyTimeOut should be set to date of new request
//
//        // Absent -> permission
//        // userid, reason, permissionflag, attendanceid, timein from db_util.companyTimeIn(date), timeout from db_util.companyTimeOut(date)
//
//        // Absent -> leave
//        // userid, reason, leaveflag, attendanceid, timein from db_util.companyTimeIn(date), timeout from db_util.companyTimeOut(date)
//
//        // Absent -> present
//        // userid, reason, presentflag, attendanceid, timein from user input, timeout from user input
//
//        // Present -> present/Leave -> leave/Permission -> permission
//        // userid, reason, attendanceid, timein from user input, timeout from user input
//
//        db_util.checkCorrectionRequestExist(db, attendance.attendanceid!!) { exist ->
//            // Check whether selected date already have correction request or not
//            if (exist != null) {
//                if (!exist) {
//                    val temp_correction = CorrectionRequest(
//                        userid = user.userid,
//                        timein = Date(),
//                        timeout = Date(),
//                        reason = "testing",
//                        leaveflag = false,
//                        permissionflag = false,
//                        presentflag = false,
//                        attendanceid = attendance.attendanceid
//                    );
//
//                    db_util.checkPendingRequestDuration(db, user.userid!!, temp_correction.timein!!) { leaveamt, permamt ->
//                        if (leaveamt != null) {
//                            db_util.getCompanyParams(db) { companyParams ->
//                                if (companyParams != null) {
//                                    if (temp_correction.permissionflag!!) {
//                                        db_util.getTotalPermissionThisYear(db, user.userid) { data ->
//                                            if (data != null) {
//                                                if (1 + data + permamt!! > companyParams.maxpermissionsleft!!) {
//                                                    // Put error popup here on frontend
//                                                    Log.e("CREATECORRECTIONREQUEST", "Not enough permissions left to create request")
//                                                } else {
//                                                    db_util.createCorrectionRequest(db, temp_correction)
//                                                }
//                                            }
//                                        }
//                                    } else if (temp_correction.leaveflag!!) {
//                                        if (user.leaveallow!!) {
//                                            db_util.getTotalLeaveThisMonth(db, user.userid) { data ->
//                                                if (data != null) {
//                                                    if (1 + data + leaveamt!! > companyParams.maxmonthlyleaveleft!!) {
//                                                        // Put error popup here on frontend
//                                                        Log.e("CREATECORRECTIONREQUEST", "Leave request exceeds monthly quota")
//                                                    } else if (1 + leaveamt!! > user.leaveleft!!) {
//                                                        // Put error popup here on frontend
//                                                        Log.e("CREATECORRECTIONREQUEST", "Not enough leave left to create request")
//                                                    } else {
//                                                        db_util.createCorrectionRequest(db, temp_correction)
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            // Put error popup here on frontend
//                                            Log.e("CREATECORRECTIONREQUEST", "Leave not allowed for currrent user")
//                                        }
//                                    } else if (temp_correction.presentflag!!) {
//                                        db_util.createCorrectionRequest(db, temp_correction)
//                                    } else {
//                                        if (attendance.permissionflag!! || attendance.leaveflag!!) {
//                                            db_util.checkValidCorrectionRequestDate(db, user.userid, temp_correction.timein){ valid ->
//                                                if (valid != null) {
//                                                    if (valid == false) {
//                                                        // Put error popup here on frontend
//                                                        Log.e("CREATECORRECTIONREQUEST", "New selected date overlaps with an existing attendance/request")
//                                                    } else {
//                                                        db_util.createCorrectionRequest(db, temp_correction)
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            db_util.createCorrectionRequest(db, temp_correction)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    // Put error popup here on frontend
//                    Log.e("CREATECORRECTIONREQUEST", "Correction request already exists for selected date")
//                }
//            }
//        }
//    }

//    fun testCancelLeaveRequest(db:FirebaseFirestore, leaverequestid: String){
//        db_util.cancelLeaveRequest(db, leaverequestid) { result ->
//            if (result) {
//                // Delete success
//            } else {
//                // Admin has already approved/rejected the request
//            }
//        }
//    }

    fun testCancelCorrectionRequest(db:FirebaseFirestore, correctionrequestid: String){
        db_util.cancelCorrectionRequest(db, correctionrequestid) { result ->
            if (result) {
                // Delete success
            } else {
                // Admin has already approved/rejected the request
            }
        }
    }

    // User here refers to the admin whose approving the request (current logged in user)
//    fun testApproveLeaveRequest(db: FirebaseFirestore, leaverequest: LeaveRequest, user: User, companyparams: CompanyParams){
//        db_util.approveLeaveRequest(db, leaverequest, user, companyparams)
//    }

    // User here refers to the admin whose approving the request (current logged in user)
    fun testApproveCorrectionRequest(db: FirebaseFirestore, correctionrequest: CorrectionRequest, user: User, companyparams: CompanyParams){
        db_util.approveCorrectionRequest(db, correctionrequest, user, companyparams)
    }

    fun testRejectLeaveRequest(db:FirebaseFirestore, leaverequestid: String, userid: String){
        db_util.rejectLeaveRequest(db, leaverequestid, userid);
    }

    fun testRejectCorrectionRequest(db: FirebaseFirestore,correctionrequestid: String, userid: String){
        db_util.rejectCorrectionRequest(db, correctionrequestid, userid);
    }
}