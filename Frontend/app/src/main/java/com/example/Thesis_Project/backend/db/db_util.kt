package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

object db_util {

    fun getUser(db: FirebaseFirestore, userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<User>();
                    callback(user);
                } else {
                    callback(null);
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetching Data", "getUser: $exception");
                callback(null);
            }
    }
    // userid == null -> get all users
    fun getAttendance(db: FirebaseFirestore, userId: String?, dateStart: Date, dateEnd: Date, callback: (List<Attendance>?) -> Unit){
        var query = db.collection("attendances")
            .whereGreaterThanOrEqualTo("timein",dateStart)
            .whereLessThanOrEqualTo("timein",dateEnd)

        if(userId != null){
            query = query.whereEqualTo("userid",userId)
        }

        query.get()
        .addOnSuccessListener { querySnapshot ->
            val attendances = mutableListOf<Attendance>();
            for(i in querySnapshot){
                val temp = i.toObject<Attendance>();
                attendances.add(temp);
            }
            callback(attendances);
        }
        .addOnFailureListener { exception->
            Log.e("Error Fetching Data", "getAttendance $exception");
            callback(null);
        }
    }

    // userid == null -> get all users
    fun getLeaveRequest(db: FirebaseFirestore, userId: String?, callback: (List<LeaveRequest>?) -> Unit){
        val col = db.collection("leave_requests");
        var query: Query = col;
        if(userId != null){
            query = query.whereEqualTo("userid",userId);
        }

        query.orderBy("createdate",Query.Direction.DESCENDING).get()
            .addOnSuccessListener { querySnapshot ->
                val leaverequests = mutableListOf<LeaveRequest>();
                for(i in querySnapshot){
                    val temp = i.toObject<LeaveRequest>();
                    leaverequests.add(temp);
                }
                callback(leaverequests);
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data", "getLeaveRequest $exception");
                callback(null);
            }
    }

    // userid == null -> get all users
    fun getCorrectionRequest(db: FirebaseFirestore, userId: String?, callback: (List<CorrectionRequest>?) -> Unit){
        val col = db.collection("correction_requests");
        var query: Query = col;
        if(userId != null){
            query = query.whereEqualTo("userid",userId);
        }

        query.orderBy("createdate",Query.Direction.DESCENDING).get()
            .addOnSuccessListener { querySnapshot ->
                val correctionrequests = mutableListOf<CorrectionRequest>();
                for(i in querySnapshot){
                    val temp = i.toObject<CorrectionRequest>();
                    correctionrequests.add(temp);
                }
                callback(correctionrequests);
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data", "getCorrectionRequest $exception");
                callback(null);
            }
    }

    fun createAttendance(db: FirebaseFirestore, data: Attendance){
        val collection = db.collection("attendances").document();
        data.attendanceid = collection.id;
        db.collection("attendances").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATEATTENDANCE","Attendance created with id ${collection.id}");
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createAttendance $exception");
            }
    }

    fun createLeaveRequest(db:FirebaseFirestore, data: LeaveRequest){
        val collection = db.collection("leave_requests").document();
        data.leaverequestid = collection.id;
        data.createdate = curDateTime();
        db.collection("leave_requests").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATELEAVEREQUEST","Leave request created with id ${collection.id}");
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createLeaveRequest $exception");
            }
    }

    fun createCorrectionRequest(db:FirebaseFirestore, data: CorrectionRequest){
        val collection = db.collection("correction_requests").document();
        data.correctionrequestid = collection.id;
        data.createdate = curDateTime();
        db.collection("correction_requests").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATECORRECTIONREQUEST","Correction request created with id ${collection.id}");
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createCorrectionRequest $exception");
            }
    }

    fun rejectLeaveRequest(db: FirebaseFirestore, leaverequestid: String, userid: String){
        db.collection("leave_requests").document(leaverequestid)
            .update("rejectedflag",true,"rejectedtime", curDateTime(),"rejectedby",userid)
            .addOnSuccessListener {
                Log.d("REJECTLEAVEREQUEST","Leave request id $leaverequestid successfully rejected");
            }
            .addOnFailureListener { exception ->
                Log.e("Error Updating Data","rejectLeaveRequest $exception");
            }
    }

    fun rejectCorrectionRequest(db: FirebaseFirestore, correctionrequestid: String, userid: String){
        db.collection("correction_requests").document(correctionrequestid)
            .update("rejectedflag",true,"rejectedtime", curDateTime(),"rejectedby",userid)
            .addOnSuccessListener {
                Log.d("REJECTCORRECTIONREQUEST","Correction request id $correctionrequestid successfully rejected");
            }
            .addOnFailureListener { exception ->
                Log.e("Error Updating Data","rejectCorrectionRequest $exception");
            }
    }

    fun curDateTime(): Date{
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    fun firstDateOfMonth(date: LocalDate=LocalDate.now()):Date{
        return Date.from(date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    fun lastDateOfMonth(date: LocalDate=LocalDate.now()): Date{
        return Date.from(date.plusMonths(1).withDayOfMonth(1).minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }
}