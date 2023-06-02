package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.Attendance
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
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

        if(userId != null){
            query = query.whereEqualTo("userid",userId)
        }

        query.get()
        .addOnSuccessListener { querySnapshot ->
            val attendances = mutableListOf<Attendance>();
            for(i in querySnapshot){
                val temp = i.toObject<Attendance>();
                if(temp.timeout!! <= dateEnd){
                    attendances.add(temp);
                }
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

        query.get()
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

        query.get()
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
}