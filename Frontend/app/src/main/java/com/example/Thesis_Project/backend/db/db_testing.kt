package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.util.*


object db_testing {

    fun runTests(db: FirebaseFirestore,userid: String){
//        // For get, if userid == null, will get for all users
//        testGetAttendance(db,userid);
//        testGetLeaveRequest(db,userid);
//        testGetCorrectionRequest(db,userid);
//
//        testCreateAttendance(db,userid);
//        testCreateLeaveRequest(db,userid,5) // Leave left parameter only temporary, Frontend should not actually send it and instead read from userdata ViewModel or something;
//        testCreateCorrectionRequest(db,userid);
//
//        testRejectCorrectionRequest(db,"nDG36ZOgWGVvaNVeeqQG",userid);
//        testRejectLeaveRequest(db,"gFgsZdwLeQQA45X9bRtV",userid);
        db_util.approveLeaveRequest(db,"s8r3vWtVPwgIqs6KGAp2","vMQz8RTu4iR7pJMLlrnN","vMQz8RTu4iR7pJMLlrnN")
    }
    fun testGetAttendance(db: FirebaseFirestore, userid: String? = null){
        val datestart = db_util.firstDateOfMonth();
        val dateend = db_util.lastDateOfMonth();

        db_util.getAttendance(db, userid,datestart,dateend) {data ->
            if(data != null){
                if(data.isNotEmpty()) {
                    for (i in data) {
                        Log.d("ATTENDANCEDATA", i.timein!!.toString())
                    }
                }
                else{
                    Log.e("ATTENDANCEDATA", "Attendance not found") // Error because there is no data in db
                }
            } else {
                Log.e("ATTENDANCEDATA", "Attendance not found")  // Error because firestore fetch failed
            }
        }
    }

    fun testGetLeaveRequest(db:FirebaseFirestore, userid: String? = null){
        db_util.getLeaveRequest(db, userid) {data ->
            if(data != null){
                if(data.isNotEmpty()) {
                    for (i in data) {
                        Log.d("LEAVEREQDATA", i.createdate!!.toString())
                    }
                }
                else{
                    Log.e("LEAVEREQDATA", "Leave request not found") // Error because there is no data in db
                }
            } else {
                Log.e("LEAVEREQDATA", "Leave request not found")  // Error because firestore fetch failed
            }
        }
    }

    fun testGetCorrectionRequest(db: FirebaseFirestore, userid: String?){
        db_util.getCorrectionRequest(db, userid) {data ->
            if(data != null){
                if(data.isNotEmpty()) {
                    for (i in data) {
                        Log.d("CORRECTREQDATA", i.createdate!!.toString())
                    }
                }
                else{
                    Log.e("CORRECTREQDATA", "Correction request not found") // Error because there is no data in db
                }
            } else {
                Log.e("CORRECTREQDATA", "Correction request not found")  // Error because firestore fetch failed
            }
        }
    }

    fun testCreateAttendance(db: FirebaseFirestore, userid: String){
        val temp_attendance = Attendance(
            userid=userid,
            leaveflag=false,
            permissionflag = false,
            absentflag = false,
            timein = db_util.curDateTime())
        db_util.createAttendance(db,temp_attendance);
    }

    fun testCreateLeaveRequest(db: FirebaseFirestore,userid: String, leaveleft: Int){
        // Frontend should check whether user leaveallow = true or not here
        // Don't actually need to pass leaveleft parameter if user data is stored in a ViewModel later
        // leaveleft parameter only used here because I don't want to get user data again
        db_util.checkPendingLeaveRequestExist(db,userid){data ->
            if(data == false){
                val temp_leave = LeaveRequest(
                    userid=userid,
                    leavestart=db_util.firstDateOfMonth(),
                    leaveend=db_util.firstDateOfMonth(LocalDate.now().plusDays(3)),
                    permissionflag = false,
                    reason="testing");
                val duration = db_util.calcDurationDays(temp_leave.leavestart!!,temp_leave.leaveend!!)

                // Frontend should probably get company params and save it to ViewModel or somewhere so don't have to keep calling getCompanyParams
                db_util.getCompanyParams(db){companyParams->
                    if(companyParams != null){
                        if(temp_leave.permissionflag!!){
                            db_util.getTotalPermissionThisYear(db,userid){data ->
                                if(data != null){
                                    if(duration + data > companyParams.maxpermissionsleft!!){
                                        // Put popup permissions left not enough, can create but will deduct leave left
                                        // If leaveleft not enough will count as absent
                                        // If user agrees run createLeaveRequest
                                        db_util.createLeaveRequest(db,temp_leave)

                                        // If user disagrees close popup and do nothing
                                    }
                                    else{
                                        db_util.createLeaveRequest(db,temp_leave)
                                    }
                                }
                            }
                        }
                        else {
                            db_util.getTotalLeaveThisMonth(db,userid){data ->
                                if(data != null){
                                    if(duration + data > companyParams.maxmonthlyleaveleft!!){
                                        // Put error popup here on frontend
                                        Log.e("CREATELEAVEREQUEST","Leave request exceeds monthly quota")
                                    }
                                    else if(duration > leaveleft){
                                        // Put error popup here on frontend
                                        Log.e("CREATELEAVEREQUEST", "Not enough leave left to create request")
                                    }
                                    else{
                                        db_util.createLeaveRequest(db,temp_leave)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun testCreateCorrectionRequest(db: FirebaseFirestore,userid: String){
        val temp_correction = CorrectionRequest(
            userid=userid,
            timein= Date(),
            timeout= Date(),
            reason="testing");
        db_util.createCorrectionRequest(db,temp_correction);
    }

    fun testRejectLeaveRequest(db:FirebaseFirestore, leaverequestid: String, userid: String){
        db_util.rejectLeaveRequest(db,leaverequestid,userid);
    }

    fun testRejectCorrectionRequest(db: FirebaseFirestore,correctionrequestid: String, userid: String){
        db_util.rejectCorrectionRequest(db,correctionrequestid,userid);
    }
}