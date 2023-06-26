package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.*
import java.time.temporal.TemporalAdjusters.firstDayOfYear
import java.time.temporal.TemporalAdjusters.lastDayOfYear
import java.util.Date

object db_util {
    suspend fun getUser(db: FirebaseFirestore, userId: String, callback: (User?) -> Unit) {
        try {
            val documentSnapshot = db.collection("users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                callback(documentSnapshot.toObject<User>())
            } else {
                callback(null)
            }
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getUser: $exception")
            callback(null)
        }
//        db.collection("users").document(userId).get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val user = documentSnapshot.toObject<User>()
//                    callback(user)
//                } else {
//                    callback(null)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Error Fetching Data", "getUser: $exception")
//                callback(null)
//            }
    }

    suspend fun getAllUser(db: FirebaseFirestore, callback: (List<User>?) -> Unit) {
        try {
            val querySnapshot = db.collection("users").get().await()
            val userList = mutableListOf<User>()
            for (document in querySnapshot.documents) {
                val user = document.toObject<User>()
                if (user != null) {
                    userList.add(user)
                }
            }
            callback(userList)
        } catch (e: Exception) {
            Log.e("Error Fetch Data", "getallUser $e")
            callback(null)
        }
//        val temp = mutableListOf<User>()
//        db.collection("users").get()
//            .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    for (i in querySnapshot) {
//                        temp.add(i.toObject<User>())
//                    }
//                }
//                Log.e("Fetch Data", "getallUser successfully")
//                callback(temp)
//            }
//            .addOnFailureListener { exception ->
//
//            }
    }

    suspend fun createUser(
        db: FirebaseFirestore,
        user: User,
        userId: String,
        companyparams: CompanyParams
    ) {
        val doc = db.collection("users").document(userId)
        val map: MutableMap<String, Int> = mutableMapOf<String, Int>()
        for (i in 1..12) {
            map[i.toString()] = companyparams.toleranceworktime!!
        }
        user.joindate = curDateTime()
        user.leaveleft = 0
        user.notelastupdated = curDateTime()
        user.monthlytoleranceworktime = map
        user.note = ""
        user.userid = userId

        try {
            doc.set(user).await()
            Log.d("CREATEUSER", "User successfully created with id ${user.userid}")
        } catch (exception: Exception) {
            Log.e("Error Creating Data", "createuser $exception")
        }
    }

    suspend fun createUserAuth(
        createUserAuth: FirebaseAuth,
        db: FirebaseFirestore,
        user: User,
        email: String,
        password: String,
        companyparams: CompanyParams
    ) {
        try {
            createUserAuth.createUserWithEmailAndPassword(email, password).await()
            val createdUser = createUserAuth.currentUser
            Log.d("Create User Auth", "User successfully created with id $user")
            if (createdUser != null) {
                createUser(db, user, createdUser.uid, companyparams)
            }
            createUserAuth.signOut()
        } catch (exception: Exception) {
            Log.e("Error Creating User Auth", "createuserauth $exception")
        }
    }

    suspend fun checkUserIsAdmin(
        db: FirebaseFirestore,
        userid: String,
        callback: (Boolean?) -> Unit
    ) {
        withContext(
            Dispatchers.Main
        ) {
            try {
                val snapshot = db.collection("users").document(userid).get().await()
                if (snapshot.exists()) {
                    val temp = snapshot.toObject<User>()
                    if (temp != null) {
                        if (temp.adminflag == true) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    Log.e("CHECKUSERISADMIN", "User not found")
                    callback(false)
                }
            } catch (exception: Exception) {
                Log.e("Error Fetch Data", "checkUserIsAdmin $exception")
                callback(null)
            }
        }

    }

    suspend fun updateUserNote(db: FirebaseFirestore, user: User) {
        try {
            db.collection("users").document(user.userid!!)
                .update("note", user.note, "notelastupdated", curDateTime())
                .await()
            Log.d("UPDATEUSERNOTE", "Note successfully updated")
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "updateUserNote $exception")
        }
    }


    // userid == null -> get all users
    suspend fun getAttendance(
        db: FirebaseFirestore,
        userId: String?,
        dateStart: Date,
        dateEnd: Date,
        callback: (List<Attendance>?) -> Unit
    ) {
        try {
            var query = db.collection("attendances")
                .whereGreaterThanOrEqualTo("timein", dateStart)
                .whereLessThanOrEqualTo("timein", dateEnd)

            if (userId != null) {
                query = query.whereEqualTo("userid", userId)
            }

            val querySnapshot = query.get().await()
            val attendances = mutableListOf<Attendance>()
            for (i in querySnapshot) {
                val temp = i.toObject<Attendance>()
                attendances.add(temp)
            }
            callback(attendances)
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getAttendance $exception")
            callback(null)
        }
    }

    suspend fun getSuspendAttendance(
        db: FirebaseFirestore,
        userId: String?,
        dateStart: Date,
        dateEnd: Date,
        callback: suspend (List<Attendance>?) -> Unit
    ) {
        try {
            var query = db.collection("attendances")
                .whereGreaterThanOrEqualTo("timein", dateStart)
                .whereLessThanOrEqualTo("timein", dateEnd)

            if (userId != null) {
                query = query.whereEqualTo("userid", userId)
            }

            val querySnapshot = query.get().await()
            val attendances = mutableListOf<Attendance>()
            for (i in querySnapshot) {
                val temp = i.toObject<Attendance>()
                attendances.add(temp)
            }
            callback(attendances)
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getAttendance $exception")
            callback(null)
        }
    }

    // userid == null -> get all users
    suspend fun getLeaveRequest(
        db: FirebaseFirestore,
        userId: String?,
        callback: (List<LeaveRequest>?) -> Unit
    ) {
        try {
            val col = db.collection("leave_requests")
            var query: Query = col
            if (userId != null) {
                query = query.whereEqualTo("userid", userId)
            }

            val querySnapshot =
                query.orderBy("createdate", Query.Direction.DESCENDING).get().await()
            val leaverequests = mutableListOf<LeaveRequest>()
            for (i in querySnapshot) {
                val temp = i.toObject<LeaveRequest>()
                leaverequests.add(temp)
            }
            callback(leaverequests)
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getLeaveRequest $exception")
            callback(null)
        }
//        val col = db.collection("leave_requests")
//        var query: Query = col
//        if (userId != null) {
//            query = query.whereEqualTo("userid", userId)
//        }
//
//        query.orderBy("createdate", Query.Direction.DESCENDING).get()
//            .addOnSuccessListener { querySnapshot ->
//                val leaverequests = mutableListOf<LeaveRequest>()
//                for (i in querySnapshot) {
//                    val temp = i.toObject<LeaveRequest>()
//                    leaverequests.add(temp)
//                }
//                callback(leaverequests)
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Error Fetching Data", "getLeaveRequest $exception")
//                callback(null)
//            }
    }

    // userid == null -> get all users
    suspend fun getCorrectionRequest(
        db: FirebaseFirestore,
        userId: String?,
        callback: (List<CorrectionRequest>?) -> Unit
    ) {
        try {
            val col = db.collection("correction_requests")
            var query: Query = col
            if (userId != null) {
                query = query.whereEqualTo("userid", userId)
            }

            val querySnapshot =
                query.orderBy("createdate", Query.Direction.DESCENDING).get().await()
            val correctionRequests = mutableListOf<CorrectionRequest>()
            for (i in querySnapshot) {
                val temp = i.toObject<CorrectionRequest>()
                correctionRequests.add(temp)
            }
            callback(correctionRequests)
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getCorrectionRequest $exception")
            callback(null)
        }
//        val col = db.collection("correction_requests")
//        var query: Query = col
//        if (userId != null) {
//            query = query.whereEqualTo("userid", userId)
//        }
//
//        query.orderBy("createdate", Query.Direction.DESCENDING).get()
//            .addOnSuccessListener { querySnapshot ->
//                val correctionrequests = mutableListOf<CorrectionRequest>()
//                for (i in querySnapshot) {
//                    val temp = i.toObject<CorrectionRequest>()
//                    correctionrequests.add(temp)
//                }
//                callback(correctionrequests)
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Error Fetching Data", "getCorrectionRequest $exception")
//                callback(null)
//            }
    }

    suspend fun getTotalLeaveThisMonth(
        db: FirebaseFirestore,
        userid: String,
        callback: suspend (Int?) -> Unit
    ) {
        try {
            val querySnapshot = db.collection("attendances")
                .whereEqualTo("userid", userid)
                .whereGreaterThanOrEqualTo("timein", firstDateOfMonth())
                .whereLessThanOrEqualTo("timein", lastDateOfMonth())
                .whereEqualTo("leaveflag", true)
                .whereEqualTo("permissionflag", false).get()
                .await()
            callback(querySnapshot.size())
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getTotalLeaveThisMonth $exception")
            callback(null)
        }
    }

    suspend fun getTotalPermissionThisYear(
        db: FirebaseFirestore,
        userid: String,
        callback: suspend (Int?) -> Unit
    ) {
        try {
            val querySnapshot = db.collection("attendances")
                .whereEqualTo("userid", userid)
                .whereGreaterThanOrEqualTo("timein", firstDateOfYear())
                .whereLessThanOrEqualTo("timein", lastDateOfYear())
                .whereEqualTo("permissionflag", true)
                .get()
                .await()
            callback(querySnapshot.size())
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getCompanyParams $exception")
            callback(null)
        }
    }

    suspend fun getCompanyParams(
        db: FirebaseFirestore,
        callback: (CompanyParams?) -> Unit
    ) {
        try {
            val documentSnapshot =
                db.collection("company_params").document("COMPANYPARAMS").get().await()
            if (documentSnapshot.exists()) {
                callback(documentSnapshot.toObject<CompanyParams>())
            } else {
                callback(null)
            }
        } catch (exception: Exception) {
            Log.e("Error Fetching Data", "getCompanyParams $exception")
            callback(null)
        }
//        db.collection("company_params").document("COMPANYPARAMS").get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    callback(documentSnapshot.toObject<CompanyParams>())
//                } else {
//                    callback(null)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Error Fetching Data", "getCompanyParams $exception")
//                callback(null)
//            }
    }

    fun createAttendance(db: FirebaseFirestore, data: Attendance, user: User) {
        val collection = db.collection("attendances").document()
        val userref = db.collection("users").document(user.userid!!)
        data.attendanceid = collection.id
        db.runTransaction { transaction ->
            transaction.set(collection, data)
            transaction.update(userref, "embedding", user.embedding)
            null
        }
            .addOnSuccessListener {
                Log.d("CREATEATTENDANCE", "Attendance created with id ${collection.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createAttendance $exception")
            }
    }

    fun registerFace(db: FirebaseFirestore, userid: String, embs: String) {
        db.collection("users").document(userid)
            .update("embedding", embs)
            .addOnSuccessListener {
                Log.d("REGISTERFACE", "Face successfully registered")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Updating Data", "registerface $exception")
            }
    }

    suspend fun createLeaveRequest(db: FirebaseFirestore, data: LeaveRequest) {
        val collection = db.collection("leave_requests").document()
        data.leaverequestid = collection.id
        data.createdate = curDateTime()
        try {
            collection.set(data).await()
            Log.d("CREATELEAVEREQUEST", "Leave request created with id ${collection.id}")
        } catch (exception: Exception) {
            Log.e("Error Creating Data", "createLeaveRequest $exception")
        }
    }

    suspend fun createCorrectionRequest(db: FirebaseFirestore, data: CorrectionRequest) {
        try {
            val collection = db.collection("correction_requests").document()
            data.correctionrequestid = collection.id
            data.createdate = curDateTime()
            db.collection("correction_requests").document(collection.id).set(data)
                .await()
            Log.d(
                "CREATECORRECTIONREQUEST",
                "Correction request created with id ${collection.id}"
            )
        } catch (exception: Exception) {
            Log.e("Error Creating Data", "createCorrectionRequest $exception")
        }
    }

    suspend fun rejectLeaveRequest(db: FirebaseFirestore, leaverequestid: String, userid: String) {
        try {
            db.collection("leave_requests").document(leaverequestid)
                .update(
                    "rejectedflag", true,
                    "rejectedtime", curDateTime(),
                    "rejectedby", userid
                )
                .await()
            Log.d(
                "REJECTLEAVEREQUEST",
                "Leave request id $leaverequestid successfully rejected"
            )
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "rejectLeaveRequest $exception")
        }

    }

    suspend fun rejectCorrectionRequest(
        db: FirebaseFirestore,
        correctionrequestid: String,
        userid: String
    ) {
        try {
            db.collection("correction_requests").document(correctionrequestid)
                .update(
                    "rejectedflag", true,
                    "rejectedtime", curDateTime(),
                    "rejectedby", userid
                )
                .await()
            Log.d(
                "REJECTCORRECTIONREQUEST",
                "Correction request id $correctionrequestid successfully rejected"
            )
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "rejectCorrectionRequest $exception")
        }
    }

    // User here refers to the admin whose approving the request
    suspend fun approveLeaveRequest(
        db: FirebaseFirestore,
        leaverequest: LeaveRequest,
        user: User,
        companyparams: CompanyParams
    ) {
        val leavereqref = db.collection("leave_requests").document(leaverequest.leaverequestid!!)
        val userref = db.collection("users").document(leaverequest.userid!!)
        getTotalPermissionThisYear(db, leaverequest.userid) { data ->
            if (data != null) {
                var permissionsleft = data
                db.runTransaction { transaction ->
                    val usersnapshot = transaction.get(userref)
                    val startdate = leaverequest.leavestart
                    val duration = leaverequest.duration
                    val permissionflag = leaverequest.permissionflag
                    val tapintime = companyparams.tapintime!!.split(":")
                    val tapouttime = companyparams.tapouttime!!.split(":")
                    val worktime = companyparams.companyworktime!!.toInt()
                    val maxpermissionsleft = companyparams.maxpermissionsleft!!.toInt()
                    var leaveleft = usersnapshot.getLong("leaveleft")!!.toInt()
                    permissionsleft = maxpermissionsleft - permissionsleft!!
                    transaction.update(
                        leavereqref,
                        "approvedby",
                        user.userid,
                        "approvedflag",
                        true,
                        "approvedtime",
                        curDateTime()
                    )
                    if (permissionflag!!) {
                        var leavecount = 0

                        for (i in 0 until duration!!) {
                            val timein = dateToLocalDate(startdate!!).plusDays(i.toLong())
                                .atTime(tapintime[0].toInt(), tapintime[1].toInt())
                            val timeout =
                                timein.withHour(tapouttime[0].toInt())
                                    .withMinute(tapouttime[1].toInt())
                            val collection = db.collection("attendances").document()
                            val attendanceref = db.collection("attendances").document(collection.id)
                            if (permissionsleft!! > 0) {
                                permissionsleft = permissionsleft!! - 1
                                val attendance = Attendance(
                                    attendanceid = collection.id,
                                    timein = localDateTimeToDate(timein),
                                    timeout = localDateTimeToDate(timeout),
                                    userid = leaverequest.userid,
                                    leaveflag = false,
                                    permissionflag = true,
                                    absentflag = false,
                                    worktime = worktime
                                )
                                transaction.set(attendanceref, attendance)
                            } else if (leaveleft > 0) {
                                leaveleft -= 1
                                leavecount += 1
                                val attendance = Attendance(
                                    attendanceid = collection.id,
                                    timein = localDateTimeToDate(timein),
                                    timeout = localDateTimeToDate(timeout),
                                    userid = leaverequest.userid,
                                    leaveflag = true,
                                    permissionflag = false,
                                    absentflag = false,
                                    worktime = worktime
                                )
                                transaction.set(attendanceref, attendance)
                            } else {
                                val attendance = Attendance(
                                    attendanceid = collection.id,
                                    timein = localDateTimeToDate(
                                        timein.toLocalDate().atStartOfDay()
                                    ),
                                    timeout = localDateTimeToDate(
                                        timein.toLocalDate().atStartOfDay()
                                    ),
                                    userid = leaverequest.userid,
                                    leaveflag = false,
                                    permissionflag = false,
                                    absentflag = true,
                                    worktime = 0
                                )
                                transaction.set(attendanceref, attendance)
                            }
                        }
                        transaction.update(
                            userref,
                            "leaveleft",
                            FieldValue.increment((-1 * leavecount).toLong())
                        )
                    } else {
                        for (i in 0 until duration!!) {
                            val timein = dateToLocalDate(startdate!!).plusDays(i.toLong())
                                .atTime(tapintime[0].toInt(), tapintime[1].toInt())
                            val timeout =
                                timein.withHour(tapouttime[0].toInt())
                                    .withMinute(tapouttime[1].toInt())
                            val collection = db.collection("attendances").document()
                            val attendance = Attendance(
                                attendanceid = collection.id,
                                timein = localDateTimeToDate(timein),
                                timeout = localDateTimeToDate(timeout),
                                userid = leaverequest.userid,
                                leaveflag = true,
                                permissionflag = false,
                                absentflag = false,
                                worktime = worktime
                            )
                            val attendanceref = db.collection("attendances").document(collection.id)
                            transaction.set(attendanceref, attendance)
                        }
                        transaction.update(
                            userref,
                            "leaveleft",
                            FieldValue.increment((-1 * duration).toLong())
                        )
                    }
                    null
                }.addOnSuccessListener {
                    Log.d(
                        "APPROVELEAVEREQUEST",
                        "Leave request id ${leaverequest.leaverequestid} successfully approved"
                    )
                }.addOnFailureListener { exception ->
                    Log.e("Error Updating Data", "approveLeaveRequest $exception")
                }
            }
        }

    }

    // User here refers to the admin whose approving the request
    suspend fun approveCorrectionRequest(
        db: FirebaseFirestore,
        correctionrequest: CorrectionRequest,
        user: User,
        companyparams: CompanyParams
    ) {
        try {
            val userref = db.collection("users").document(correctionrequest.userid!!)
            val attendanceref = db.collection("attendances").document(correctionrequest.attendanceid!!)
            val correctionreqref =
                db.collection("correction_requests").document(correctionrequest.correctionrequestid!!)

            db.runTransaction { transaction ->
                val attendancesnapshot = transaction.get(attendanceref)
                val leaveflag = attendancesnapshot.getBoolean("leaveflag")
                val permissionflag = attendancesnapshot.getBoolean("permissionflag")
                val oldworktime = attendancesnapshot.getLong("worktime")

                transaction.update(
                    correctionreqref,
                    "approvedby",
                    user.userid,
                    "approvedflag",
                    true,
                    "approvedtime",
                    curDateTime()
                )

                if (correctionrequest.permissionflag!!) {
                    transaction.update(attendanceref, "permissionflag", true, "absentflag", false)
                } else if (correctionrequest.leaveflag!!) {
                    transaction.update(attendanceref, "leaveflag", true, "absentflag", false)
                    transaction.update(userref, "leaveleft", FieldValue.increment(-1))
                } else if (correctionrequest.presentflag!!) {
                    val worktime = calcWorkTime(
                        correctionrequest.timein!!,
                        correctionrequest.timeout!!,
                        companyparams
                    )
                    transaction.update(
                        attendanceref,
                        "absentflag",
                        false,
                        "timein",
                        correctionrequest.timein,
                        "timeout",
                        correctionrequest.timeout,
                        "worktime",
                        worktime
                    )
                    if (worktime < companyparams.companyworktime!!) {
                        val newmap = user.monthlytoleranceworktime
                        newmap!![dateToLocalDate(correctionrequest.timein).month.value.toString()] =
                            newmap[dateToLocalDate(correctionrequest.timein).month.value.toString()]!! + (worktime - companyparams.companyworktime!!)
                        transaction.update(userref, "monthlytoleranceworktime", newmap)
                    }
                } else {
                    if (leaveflag!! || permissionflag!!) {
                        transaction.update(
                            attendanceref,
                            "timein",
                            correctionrequest.timein!!,
                            "timeout",
                            correctionrequest.timeout!!
                        )
                    } else {
                        val worktime = calcWorkTime(
                            correctionrequest.timein!!,
                            correctionrequest.timeout!!,
                            companyparams
                        )
                        transaction.update(
                            attendanceref,
                            "timein",
                            correctionrequest.timein,
                            "timeout",
                            correctionrequest.timeout,
                            "worktime",
                            worktime
                        )
                        val newmap = user.monthlytoleranceworktime
                        newmap!![dateToLocalDate(correctionrequest.timein).month.value.toString()] =
                            newmap[dateToLocalDate(correctionrequest.timein).month.value.toString()]!! + (worktime - oldworktime!!.toInt())
                        transaction.update(userref, "monthlytoleranceworktime", newmap)
                    }
                }
                null
            }.await()

            Log.d(
                "APPROVECORRECTIONREQUEST",
                "Correction request id ${correctionrequest.correctionrequestid} successfully approved"
            )
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "approveCorrectionRequest $exception")
        }
    }

    // First int leave, second int permission
    suspend fun checkPendingRequestDuration(
        db: FirebaseFirestore,
        userid: String,
        date: Date,
        callback: suspend (Int?, Int?) -> Unit
    ) {
        try {
            val correctionRequestsQuerySnapshot = db.collection("correction_requests")
                .whereEqualTo("userid", userid)
                .whereEqualTo("approvedby", null)
                .whereEqualTo("rejectedby", null)
                .whereLessThanOrEqualTo("timein", lastDateOfMonth(dateToLocalDate(date)))
                .whereGreaterThanOrEqualTo("timein", firstDateOfMonth(dateToLocalDate(date)))
                .get()
                .await()

            var leaveCount = 0
            var permCount = 0

            for (document in correctionRequestsQuerySnapshot) {
                val temp = document.toObject<CorrectionRequest>()
                if (temp.leaveflag == true) {
                    leaveCount++
                } else if (temp.permissionflag == true) {
                    permCount++
                }
            }

            val leaveRequestsQuerySnapshot = db.collection("leave_requests")
                .whereEqualTo("userid", userid)
                .whereEqualTo("approvedby", null)
                .whereEqualTo("rejectedby", null)
                .whereLessThanOrEqualTo("leavestart", lastDateOfMonth(dateToLocalDate(date)))
                .whereGreaterThanOrEqualTo("leavestart", firstDateOfMonth(dateToLocalDate(date)))
                .get()
                .await()

            for (document in leaveRequestsQuerySnapshot) {
                val temp = document.toObject<LeaveRequest>()
                if (temp.permissionflag == true) {
                    permCount += temp.duration ?: 0
                } else {
                    leaveCount += temp.duration ?: 0
                }
            }

            callback(leaveCount, permCount)
        } catch (exception: Exception) {
            Log.e("Error Fetch Data", "checkPendingRequestDuration $exception")
            callback(null, null)
        }
    }

    fun checkCorrectionRequestExist(
        db: FirebaseFirestore,
        attendanceid: String,
        callback: (Boolean?) -> Unit
    ) {
        db.collection("correction_requests").whereEqualTo("attendanceid", attendanceid)
            .whereEqualTo("rejectedby", null)
            .whereEqualTo("approvedby", null)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    callback(false)
                } else {
                    callback(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetch Data", "checkCorrectionRequestExist $exception")
            }
    }

    suspend fun checkValidLeaveRequestDate(
        db: FirebaseFirestore,
        userid: String,
        date: Date,
        duration: Int,
        callback: suspend (Boolean?) -> Unit
    ) {
        getSuspendAttendance(
            db,
            userid,
            startOfDay(dateToLocalDate(date)),
            endOfDay(dateToLocalDate(date).plusDays(duration - 1.toLong()))
        ) { attendance ->
            try {
                if (attendance != null) {
                    if (attendance.isEmpty()) {
                        getPendingRequestDates(db, userid) { dates ->
                            if (dates != null) {
                                var flag = true
                                val temp = mutableListOf<Date>()
                                for (i in 0 until duration) {
                                    temp.add(localDateToDate(dateToLocalDate(date).plusDays(i.toLong())))
                                }
                                for (i in dates) {
                                    for (j in temp) {
                                        if (dateToLocalDate(i) == dateToLocalDate(j)) {
                                            flag = false
                                            break
                                        }
                                    }
                                    if (!flag) {
                                        break
                                    }
                                }
                                callback(flag)
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e("Error Fetch Data", "get pending request dates $e")
                callback(null)
            }
        }
    }

    suspend fun checkValidCorrectionRequestDate(
        db: FirebaseFirestore,
        userid: String,
        date: Date,
        callback: suspend (Boolean?) -> Unit
    ) {
        try {
            getSuspendAttendance(
                db,
                userid,
                startOfDay(dateToLocalDate(date)),
                endOfDay(dateToLocalDate(date))
            ) { attendance ->
                if (attendance != null) {
                    if (attendance.isEmpty()) {
                        getPendingRequestDates(db, userid) { dates ->
                            if (dates != null) {
                                var flag = true
                                for (i in dates) {
                                    if (dateToLocalDate(i) == dateToLocalDate(date)) {
                                        flag = false
                                        break
                                    }
                                }
                                callback(flag)
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    callback(null)
                }
            }
        } catch (e: Exception){
            Log.e("Error Fetch Data", "get pending request dates $e")
            callback(null)
        }

    }

    // False -> User not yet tap in, True -> User already tap in (need to tap out)
    fun checkTapOutStatus(db: FirebaseFirestore, userid: String, callback: (Boolean?) -> Unit) {
        db.collection("attendances")
            .whereEqualTo("userid", userid)
            .whereGreaterThanOrEqualTo("timein", startOfDay(LocalDate.now()))
            .whereLessThanOrEqualTo("timein", endOfDay(LocalDate.now()))
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    callback(false)
                } else {
                    callback(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetch Data", "checkTapStatus $exception")
                callback(null)
            }
    }

    suspend fun tapOutAttendance(
        db: FirebaseFirestore,
        user: User,
        attendance: Attendance,
        companyparams: CompanyParams
    ) {
        val userref = db.collection("users").document(user.userid!!)
        val attendanceref = db.collection("attendances").document(attendance.attendanceid!!)
        val newmap = user.monthlytoleranceworktime!!
        val worktime = calcWorkTime(attendance.timein!!, curDateTime(), companyparams)
        if (worktime < companyparams.companyworktime!!) {
            newmap[LocalDate.now().month.value.toString()] =
                newmap[LocalDate.now().month.value.toString()]!! + (worktime - companyparams.companyworktime!!)
        }
        try {
            db.runTransaction { transaction ->
                transaction.update(userref, "monthlytoleranceworktime", newmap)
                transaction.update(attendanceref, "timeout", curDateTime(), "worktime", worktime)
                null
            }.await()
            Log.d("TAPOUT", "Tap out attendance successful")
        } catch (e: Exception) {
            Log.e("TAPOUT", "tapOutAttendance $e")
        }
    }

    suspend fun getHolidays(
        db: FirebaseFirestore,
        duration: Int? = null,
        callback: suspend (List<Holiday>?) -> Unit
    ) {
        try {
            val col = db.collection("holidays")
            var query: Query = col
            if (duration != null) {
                query = query.whereGreaterThanOrEqualTo(
                    "date",
                    localDateToDate(LocalDate.now().minusDays(duration.toLong()))
                )
                    .whereLessThanOrEqualTo("date", endOfDay(LocalDate.now()))
            }
            val queryResults = query.get().await()
            val holidays = mutableListOf<Holiday>()
            if (!queryResults.isEmpty) {
                for (i in queryResults) {
                    val temp = i.toObject<Holiday>()
                    temp.date = Date.from(
                        dateToLocalDate(temp.date!!).atStartOfDay()
                            .atZone(ZoneId.systemDefault()).toInstant()
                    )
                    holidays.add(temp)
                }
            }
            callback(holidays)
        } catch (e: Exception) {
            Log.e("Error Fetch Data", "getHolidays $e")
            callback(null)
        }
//        val col = db.collection("holidays")
//        var query: Query = col
//        if (duration != null) {
//            query = query.whereGreaterThanOrEqualTo(
//                "date",
//                localDateToDate(LocalDate.now().minusDays(duration.toLong()))
//            )
//                .whereLessThanOrEqualTo("date", endOfDay(LocalDate.now()))
//        }
//        query.get()
//            .addOnSuccessListener { snapshot ->
//                val holidays = mutableListOf<Holiday>()
//                if (!snapshot.isEmpty) {
//                    for (i in snapshot) {
//                        val temp = i.toObject<Holiday>()
//                        temp.date = Date.from(
//                            dateToLocalDate(temp.date!!).atStartOfDay()
//                                .atZone(ZoneId.systemDefault()).toInstant()
//                        )
//                        holidays.add(temp)
//                    }
//                }
//                callback(holidays)
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Error Fetch Data", "getHolidays $exception")
//                callback(null)
//            }
    }

    suspend fun getPendingRequestDates(
        db: FirebaseFirestore,
        userid: String,
        callback: suspend (List<Date>?) -> Unit
    ) {
        try {
            val correctionRequestsDeferred = db.collection("correction_requests")
                .whereEqualTo("userid", userid)
                .whereEqualTo("approvedby", null)
                .whereEqualTo("rejectedby", null)
                .get()
                .asDeferred()

            val leaveRequestsDeferred = db.collection("leave_requests")
                .whereEqualTo("userid", userid)
                .whereEqualTo("approvedby", null)
                .whereEqualTo("rejectedby", null)
                .get()
                .asDeferred()

            val correctionRequestsQuerySnapshot = correctionRequestsDeferred.await()
            val leaveRequestsQuerySnapshot = leaveRequestsDeferred.await()

            val dates = mutableListOf<Date>()

            for (document in correctionRequestsQuerySnapshot) {
                val temp = document.toObject<CorrectionRequest>()
                dates.add(
                    Date.from(
                        dateToLocalDate(temp.timein!!).atStartOfDay()
                            .atZone(ZoneId.systemDefault()).toInstant()
                    )
                )
            }

            for (document in leaveRequestsQuerySnapshot) {
                val temp = document.toObject<LeaveRequest>()
                for (j in 0 until temp.duration!!) {
                    dates.add(
                        Date.from(
                            dateToLocalDate(temp.leavestart!!).atStartOfDay()
                                .plusDays(j.toLong()).atZone(ZoneId.systemDefault())
                                .toInstant()
                        )
                    )
                }
            }
            callback(dates)
        } catch (exception: Exception) {
            Log.e("Error Fetch Data", "getPendingRequestDates $exception")
            callback(null)
        }
    }

    suspend fun cancelLeaveRequest(
        db: FirebaseFirestore,
        leaverequestid: String,
        callback: (Boolean) -> Unit
    ) {
        val leaveref = db.collection("leave_requests").document(leaverequestid)
        try {
            db.runTransaction { transaction ->
                val leavesnapshot = transaction.get(leaveref)
                if (leavesnapshot.exists() &&
                    leavesnapshot.getString("rejectedby") == null &&
                    leavesnapshot.getString("approvedby") == null
                ) {
                    transaction.delete(leaveref)
                    true
                } else {
                    throw FirebaseFirestoreException(
                        "Invalid request",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }
            }.await()
            Log.d("CANCELLEAVEREQUEST", "Leave request $leaverequestid successfully cancelled")
            callback(true)
        } catch (e: Exception) {
            Log.e("Error Update Data", "cancelLeaveRequest $e")
            callback(false)
        }

    }

    suspend fun cancelCorrectionRequest(
        db: FirebaseFirestore,
        correctionrequestid: String,
        callback: (Boolean) -> Unit
    ) {
        val correctionref = db.collection("correction_requests").document(correctionrequestid)
        try {
            db.runTransaction { transaction ->
                val correctionsnapshot = transaction.get(correctionref)
                if (correctionsnapshot.exists() &&
                    correctionsnapshot.getString("rejectedby") == null &&
                    correctionsnapshot.getString("approvedby") == null
                ) {
                    transaction.delete(correctionref)
                } else {
                    throw FirebaseFirestoreException(
                        "Invalid request",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }
                null
            }.await()

            Log.d(
                "CANCELCORRECTIONREQUEST",
                "Correction request $correctionrequestid successfully cancelled"
            )
            callback(true)
        } catch (exception: Exception) {
            Log.e("Error Update Data", "cancelCorrectionRequest $exception")
            callback(false)
        }
    }

    // Callback boolean denotes whether frontend should show popup or not (12pm auto insert timeout)
    suspend fun checkBackAttendance(
        db: FirebaseFirestore,
        user: User,
        companyparams: CompanyParams,
        callback: (Boolean?) -> Unit
    ) {
        try {
            var popupflag = false
            getSuspendAttendance(
                db,
                user.userid,
                Date.from(
                    LocalDate.now().atStartOfDay().minusDays(14).atZone(ZoneId.systemDefault())
                        .toInstant()
                ),
                curDateTime()
            ) { attendances ->
                if (attendances != null) {
                    getHolidays(db, 14) { holidays ->
                        if (holidays != null) {
                            getPendingRequestDates(db, user.userid!!) { dates ->
                                if (dates != null) {
                                    db.runTransaction { transaction ->
                                        val skippeddates = mutableListOf<LocalDate>()
                                        for (i in holidays) {
                                            skippeddates.add(dateToLocalDate(i.date!!))
                                        }
                                        for (i in dates) {
                                            skippeddates.add(dateToLocalDate(i))
                                        }
                                        for (i in attendances) {
                                            skippeddates.add(dateToLocalDate(i.timein!!))
                                        }
                                        for (i in 1..14) {
                                            val tempdate = LocalDate.now().minusDays(i.toLong())
                                            if (!skippeddates.contains(tempdate) && tempdate.dayOfWeek != DayOfWeek.SATURDAY && tempdate.dayOfWeek != DayOfWeek.SUNDAY
                                                && tempdate.isAfter(dateToLocalDate(user.joindate!!))
                                            ) {
                                                val collection =
                                                    db.collection("attendances").document()
                                                val attendanceref =
                                                    db.collection("attendances")
                                                        .document(collection.id)
                                                val data = Attendance(
                                                    attendanceid = collection.id,
                                                    userid = user.userid,
                                                    leaveflag = false,
                                                    permissionflag = false,
                                                    absentflag = true,
                                                    timein = localDateTimeToDate(tempdate.atStartOfDay()),
                                                    timeout = localDateTimeToDate(tempdate.atStartOfDay()),
                                                    worktime = 0
                                                )
                                                transaction.set(attendanceref, data)
                                            }
                                        }
                                        val userref = db.collection("users").document(user.userid!!)
                                        val newmap = user.monthlytoleranceworktime
                                        for (i in attendances) {
                                            if (i.timeout == null) {
                                                val attendanceref = db.collection("attendances")
                                                    .document(i.attendanceid!!)
                                                val timeout = localDateTimeToDate(
                                                    dateToLocalDate(i.timein!!).atTime(LocalTime.NOON)
                                                )
                                                val worktime =
                                                    calcWorkTime(i.timein!!, timeout, companyparams)
                                                newmap!![dateToLocalDate(i.timein!!).month.value.toString()] =
                                                    newmap[dateToLocalDate(i.timein!!).month.value.toString()]!! + (worktime - companyparams.companyworktime!!)
                                                transaction.update(
                                                    attendanceref,
                                                    "timeout",
                                                    timeout,
                                                    "worktime",
                                                    worktime
                                                )
                                                popupflag = true
                                            }
                                        }
                                        transaction.update(
                                            userref,
                                            "monthlytoleranceworktime",
                                            newmap
                                        )
                                        null
                                    }.await()
                                    callback(popupflag)
                                } else {
                                    Log.e(
                                        "Error Fetch Data",
                                        "checkBackAttendance getPendingDates failed"
                                    )
                                    callback(null)
                                }
                            }
                        } else {
                            Log.e("Error Fetch Data", "checkBackAttendance getHolidays failed")
                            callback(null)
                        }
                    }
                } else {
                    Log.e("Error Fetch Data", "checkBackAttendance getAttendance failed")
                    callback(null)
                }
            }
        } catch (e: Exception) {
            Log.e("Error Fetch Data", "checkBackAttendance $e")
            callback(null)
        }
    }

    suspend fun addHolidayManual(db: FirebaseFirestore, date: Date) {
        val document = db.collection("holidays").document()
        val temp = Holiday(document.id, date)
        val deleteAttendances = mutableListOf<Attendance>()
        try {
            val snapshot = db.collection("attendances")
                .whereGreaterThanOrEqualTo(
                    "timein",
                    localDateTimeToDate(dateToLocalDate(date).atStartOfDay())
                )
                .whereLessThanOrEqualTo(
                    "timein",
                    localDateTimeToDate(dateToLocalDate(date).atTime(LocalTime.MAX))
                )
                .whereEqualTo("absentflag", true)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                for (i in snapshot) {
                    deleteAttendances.add(i.toObject<Attendance>())
                }
            }

            db.runTransaction { transaction ->
                transaction.set(document, temp)
                for (i in deleteAttendances) {
                    val attRef = db.collection("attendances").document(i.attendanceid!!)
                    transaction.delete(attRef)
                }
                null
            }.await()
            Log.d("ADDHOLIDAYMANUAL", "Holiday successfully added")
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "addHolidayManual $exception")
        }
    }

    suspend fun deleteHolidayManual(db: FirebaseFirestore, holidayid: String) {
        try {
            db.collection("holidays").document(holidayid).delete().await()
            Log.d("DELETEHOLIDAYMANUAL", "Holiday successfully deleted")
        } catch (exception: Exception) {
            Log.e("Error Delete Data", "deleteHolidayManual $exception")
        }
    }

    suspend fun checkYearlyMaintenanceDone(db: FirebaseFirestore, callback: (Boolean?) -> Unit) {
        try {
            val querySnapshot = db.collection("holidays").get().await()
            if (!querySnapshot.isEmpty) {
                val temp = querySnapshot.documents[0].toObject<Holiday>()
                if (dateToLocalDate(temp!!.date!!).year == LocalDate.now().year) {
                    callback(true)
                } else {
                    callback(false)
                }
            } else {
                callback(false)
            }
        } catch (exception: Exception) {
            Log.e("CHECKYEARLYMAINTENANCEDONE", "checkYearlyMaintenanceDone $exception")
            callback(null)
        }
    }

    // dates is list of static holiday dates (new year, christmas eve, etc)
    suspend fun adminYearlyMaintenance(
        db: FirebaseFirestore,
        companyparams: CompanyParams,
        dates: List<Date>
    ) {
        try {
            val map: MutableMap<String, Int> = mutableMapOf<String, Int>()
            for (i in 1..12) {
                map[i.toString()] = companyparams.toleranceworktime!!
            }
            val users = mutableListOf<User>()
            val holidays = mutableListOf<Holiday>()

            val userSnapshot = db.collection("users").get().await()
            if (!userSnapshot.isEmpty) {
                for (i in userSnapshot) {
                    users.add(i.toObject<User>())
                }

                val holidaySnapshot = db.collection("holidays").get().await()
                if (!holidaySnapshot.isEmpty) {
                    for (i in holidaySnapshot) {
                        holidays.add(i.toObject<Holiday>())
                    }

                    db.runTransaction { transaction ->
                        for (i in users) {
                            var leaveamount = 0
                            var leaveallow = true
                            val userref = db.collection("users").document(i.userid!!)
                            if (i.leaveallow == false) {
                                val joinDate = dateToLocalDate(i.joindate!!)
                                val durationworked = calcDurationDays(i.joindate!!, curDateTime())
                                leaveallow = durationworked >= companyparams.minimumdaysworked!!
                                if (leaveallow) {
                                    leaveamount += companyparams.leaveleft!!
                                    if (durationworked <= 365) {
                                        leaveamount += 12 - joinDate.month.value
                                    }
                                } else {
                                    leaveamount += 12 - joinDate.month.value
                                }
                            } else {
                                leaveamount += companyparams.leaveleft!!
                            }
                            var leaveleft = i.leaveleft!! + leaveamount
                            if (leaveleft > companyparams.maxtotalleaveleft!!) {
                                leaveleft = companyparams.maxtotalleaveleft!!
                            }
                            transaction.update(
                                userref,
                                "monthlytoleranceworktime",
                                map,
                                "leaveallow",
                                leaveallow,
                                "leaveleft",
                                leaveleft
                            )
                        }
                        for (i in holidays) {
                            val holidayref = db.collection("holidays").document(i.holidayid!!)
                            transaction.delete(holidayref)
                        }
                        for (i in dates) {
                            val collection = db.collection("holidays").document()
                            val holidaytemp = dateToLocalDate(i)
                            val holidaynew = Date.from(
                                LocalDate.of(
                                    LocalDate.now().year,
                                    holidaytemp.month,
                                    holidaytemp.dayOfMonth
                                ).atStartOfDay().atZone(ZoneId.systemDefault())
                                    .toInstant()
                            )
                            val temp = Holiday(collection.id, holidaynew)
                            transaction.set(collection, temp)
                        }

                        null
                    }.await()
                    Log.d("YEARLYMAINTENANCE", "Successfully performed maintenance")
                }
            }
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "adminYearlyMaintenance $exception")
        }
    }

    suspend fun updateCompanyParams(db: FirebaseFirestore, companyParams: CompanyParams) {
        companyParams.companyworktime = Duration.between(
            LocalDateTime.now().withHour(companyParams.tapintime!!.split(":")[0].toInt())
                .withMinute(companyParams.tapintime!!.split(":")[1].toInt()),
            LocalDateTime.now().withHour(companyParams.tapouttime!!.split(":")[0].toInt())
                .withMinute(companyParams.tapouttime!!.split(":")[1].toInt())
        ).toMinutes().toInt()
        try {
            db.collection("company_params").document("COMPANYPARAMS").set(companyParams).await()
            Log.d("UPDATECOMPANYPARAMS", "Successfully updated company parameters")
        } catch (exception: Exception) {
            Log.e("Error Updating Data", "updateCompanyParams $exception")
        }
    }

    fun curDateTime(): Date {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun startOfDay(date: LocalDate): Date {
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun endOfDay(date: LocalDate): Date {
        return Date.from(date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
    }

    fun firstDateOfMonth(date: LocalDate = LocalDate.now()): Date {
        return Date.from(date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun lastDateOfMonth(date: LocalDate = LocalDate.now()): Date {
        return Date.from(
            date.plusMonths(1).withDayOfMonth(1).minusDays(1).atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault()).toInstant()
        )
    }

    fun firstDateOfYear(date: LocalDate = LocalDate.now()): Date {
        return Date.from(
            date.with(firstDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
    }

    fun lastDateOfYear(date: LocalDate = LocalDate.now()): Date {
        return Date.from(
            date.with(lastDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
    }

    fun calcDurationDays(datestart: Date, dateend: Date): Int {
        return Duration.between(
            datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            dateend.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        ).toDays().toInt() + 1
    }

    fun localDateToDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun dateToLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun localDateTimeToDate(date: LocalDateTime): Date {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
    }

    fun calcWorkTime(timein: Date, timeout: Date, companyparams: CompanyParams): Int {
        val tapintime = companyparams.tapintime!!.split(":")
        val timeouttemp = timeout.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val timeintemp = timein.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        var targettime = LocalDateTime.of(
            timeouttemp.year,
            timeouttemp.month,
            timeouttemp.dayOfMonth,
            tapintime[0].toInt(),
            tapintime[1].toInt()
        )
        targettime =
            targettime.plusMinutes((companyparams.companyworktime!! + companyparams.maxcompensatetime!!).toLong())
        if (timeouttemp.isAfter(targettime)) {
            var ret = Duration.between(timeintemp, targettime).toMinutes().toInt()
            if (ret > companyparams.companyworktime!!) {
                ret = companyparams.companyworktime!!
            }
            return ret
        }
        var ret = Duration.between(timeintemp, timeouttemp).toMinutes().toInt()
        if (ret > companyparams.companyworktime!!) {
            ret = companyparams.companyworktime!!
        }
        return ret
    }

    fun companyTimeIn(date: LocalDateTime, companyparams: CompanyParams): Date {
        val tapintime = companyparams.tapintime!!.split(":")
        return localDateTimeToDate(
            date.withHour(tapintime[0].toInt()).withMinute(tapintime[1].toInt())
        )
    }

    fun companyTimeOut(date: LocalDateTime, companyparams: CompanyParams): Date {
        val tapouttime = companyparams.tapouttime!!.split(":")
        return localDateTimeToDate(
            date.withHour(tapouttime[0].toInt()).withMinute(tapouttime[1].toInt())
        )
    }
}