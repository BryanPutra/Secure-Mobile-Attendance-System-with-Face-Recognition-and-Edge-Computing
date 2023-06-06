package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.time.*
import java.time.temporal.TemporalAdjusters.firstDayOfYear
import java.time.temporal.TemporalAdjusters.lastDayOfYear
import java.util.Date

object db_util {
    fun getUser(db: FirebaseFirestore, userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetching Data", "getUser: $exception")
                callback(null)
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
            val attendances = mutableListOf<Attendance>()
            for(i in querySnapshot){
                val temp = i.toObject<Attendance>()
                attendances.add(temp)
            }
            callback(attendances)
        }
        .addOnFailureListener { exception->
            Log.e("Error Fetching Data", "getAttendance $exception")
            callback(null)
        }
    }

    // userid == null -> get all users
    fun getLeaveRequest(db: FirebaseFirestore, userId: String?, callback: (List<LeaveRequest>?) -> Unit){
        val col = db.collection("leave_requests")
        var query: Query = col
        if(userId != null){
            query = query.whereEqualTo("userid",userId)
        }

        query.orderBy("createdate",Query.Direction.DESCENDING).get()
            .addOnSuccessListener { querySnapshot ->
                val leaverequests = mutableListOf<LeaveRequest>()
                for(i in querySnapshot){
                    val temp = i.toObject<LeaveRequest>()
                    leaverequests.add(temp)
                }
                callback(leaverequests)
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data", "getLeaveRequest $exception")
                callback(null)
            }
    }

    // userid == null -> get all users
    fun getCorrectionRequest(db: FirebaseFirestore, userId: String?, callback: (List<CorrectionRequest>?) -> Unit){
        val col = db.collection("correction_requests")
        var query: Query = col
        if(userId != null){
            query = query.whereEqualTo("userid",userId)
        }

        query.orderBy("createdate",Query.Direction.DESCENDING).get()
            .addOnSuccessListener { querySnapshot ->
                val correctionrequests = mutableListOf<CorrectionRequest>()
                for(i in querySnapshot){
                    val temp = i.toObject<CorrectionRequest>()
                    correctionrequests.add(temp)
                }
                callback(correctionrequests)
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data", "getCorrectionRequest $exception")
                callback(null)
            }
    }

    fun getTotalLeaveThisMonth(db: FirebaseFirestore,userid: String, callback:(Int?) -> Unit){
        db.collection("attendances")
            .whereEqualTo("userid",userid)
            .whereGreaterThanOrEqualTo("timein", firstDateOfMonth())
            .whereLessThanOrEqualTo("timein", lastDateOfMonth())
            .whereEqualTo("leaveflag",true)
            .whereEqualTo("permissionflag",false).get()
            .addOnSuccessListener {querySnapshot ->
                callback(querySnapshot.size())
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetching Data", "getTotalLeaveThisMonth $exception")
            }
    }

    fun getTotalPermissionThisYear(db: FirebaseFirestore, userid:String, callback:(Int?)->Unit){
        db.collection("attendances")
            .whereEqualTo("userid",userid)
            .whereGreaterThanOrEqualTo("timein", firstDateOfYear())
            .whereLessThanOrEqualTo("timein", lastDateOfYear())
            .whereEqualTo("permissionflag",true).get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.size())
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data","getTotalPermissionThisYear $exception")
            }
    }

    fun getCompanyParams(db: FirebaseFirestore, callback:(CompanyParams?)->Unit){
        db.collection("company_params").document("COMPANYPARAMS").get()
            .addOnSuccessListener { documentSnapshot->
                if(documentSnapshot.exists()){
                    callback(documentSnapshot.toObject<CompanyParams>())
                }
                else{
                    callback(null)
                }
            }
            .addOnFailureListener { exception->
                Log.e("Error Fetching Data","getCompanyParams $exception")
                callback(null)
            }
    }

    fun createAttendance(db: FirebaseFirestore, data: Attendance){
        val collection = db.collection("attendances").document()
        data.attendanceid = collection.id
        db.collection("attendances").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATEATTENDANCE","Attendance created with id ${collection.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createAttendance $exception")
            }
    }

    fun createLeaveRequest(db:FirebaseFirestore, data: LeaveRequest){
        val collection = db.collection("leave_requests").document()
        data.leaverequestid = collection.id
        data.createdate = curDateTime()
        db.collection("leave_requests").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATELEAVEREQUEST","Leave request created with id ${collection.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createLeaveRequest $exception")
            }
    }

    fun createCorrectionRequest(db:FirebaseFirestore, data: CorrectionRequest){
        val collection = db.collection("correction_requests").document()
        data.correctionrequestid = collection.id
        data.createdate = curDateTime()
        db.collection("correction_requests").document(collection.id).set(data)
            .addOnSuccessListener {
                Log.d("CREATECORRECTIONREQUEST","Correction request created with id ${collection.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Creating Data", "createCorrectionRequest $exception")
            }
    }

    fun rejectLeaveRequest(db: FirebaseFirestore, leaverequestid: String, userid: String){
        db.collection("leave_requests").document(leaverequestid)
            .update("rejectedflag",true,"rejectedtime", curDateTime(),"rejectedby",userid)
            .addOnSuccessListener {
                Log.d("REJECTLEAVEREQUEST","Leave request id $leaverequestid successfully rejected")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Updating Data","rejectLeaveRequest $exception")
            }
    }

    fun rejectCorrectionRequest(db: FirebaseFirestore, correctionrequestid: String, userid: String){
        db.collection("correction_requests").document(correctionrequestid)
            .update("rejectedflag",true,"rejectedtime", curDateTime(),"rejectedby",userid)
            .addOnSuccessListener {
                Log.d("REJECTCORRECTIONREQUEST","Correction request id $correctionrequestid successfully rejected")
            }
            .addOnFailureListener { exception ->
                Log.e("Error Updating Data","rejectCorrectionRequest $exception")
            }
    }

    // User here refers to the admin whose approving the request
    fun approveLeaveRequest(db: FirebaseFirestore, leaverequest: LeaveRequest, user: User, companyparams: CompanyParams) {
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
                        leavereqref, "approvedby", user.userid, "approvedflag", true, "approvedtime",
                        curDateTime()
                    )
                    if (permissionflag!!) {
                        var leavecount = 0

                        for (i in 0 until duration!!) {
                            val timein = dateToLocalDate(startdate!!).plusDays(i.toLong())
                                .atTime(tapintime[0].toInt(), tapintime[1].toInt())
                            val timeout =
                                timein.withHour(tapouttime[0].toInt()).withMinute(tapouttime[1].toInt())
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
                                    timein = localDateTimeToDate(timein),
                                    timeout = localDateTimeToDate(timeout),
                                    userid = leaverequest.userid,
                                    leaveflag = false,
                                    permissionflag = false,
                                    absentflag = true,
                                    worktime = worktime
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
                                timein.withHour(tapouttime[0].toInt()).withMinute(tapouttime[1].toInt())
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
                    Log.e("APPROVELEAVEREQUEST", "approveLeaveRequest $exception")
                }
            }
        }

    }

    // First int leave, second int permission
    fun checkPendingRequestDuration(db: FirebaseFirestore, userid: String, date: Date,callback: (Int?,Int?)->Unit){
        db.collection("correction_requests")
            .whereEqualTo("userid",userid)
            .whereEqualTo("approvedby",null)
            .whereEqualTo("rejectedby",null)
            .whereLessThanOrEqualTo("timein", lastDateOfMonth(dateToLocalDate(date)))
            .whereGreaterThanOrEqualTo("timein", firstDateOfMonth(dateToLocalDate(date)))
            .get()
            .addOnSuccessListener { querySnapshot ->
                var leavecount = 0
                var permcount = 0
                for(i in querySnapshot){
                    val temp = i.toObject<CorrectionRequest>()
                    if(temp.leaveflag!!){
                        leavecount += 1
                    }
                    else if(temp.permissionflag!!){
                        permcount +=1
                    }
                }
                db.collection("leave_requests")
                    .whereEqualTo("userid",userid)
                    .whereEqualTo("approvedby",null)
                    .whereEqualTo("rejectedby",null)
                    .whereLessThanOrEqualTo("leavestart", lastDateOfMonth(dateToLocalDate(date)))
                    .whereGreaterThanOrEqualTo("leavestart", firstDateOfMonth(dateToLocalDate(date)))
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for(i in snapshot){
                            val temp = i.toObject<LeaveRequest>()
                            if(temp.permissionflag!!){
                                permcount += temp.duration!!
                            }
                            else{
                                leavecount += temp.duration!!
                            }
                        }
                        callback(leavecount,permcount)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Fetch Data Failed","checkPendingLeaveRequestDuration $exception")
                        callback(null,null)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Fetch Data Failed","checkPendingCorrectionRequestDuration $exception")
                callback(null,null)
            }
    }

    // False -> User not yet tap in, True -> User already tap in (need to tap out)
    fun checkTapOutStatus(db: FirebaseFirestore, userid: String, callback: (Boolean?) -> Unit){
        db.collection("attendances")
            .whereEqualTo("userid",userid)
            .whereGreaterThanOrEqualTo("timein", startOfDay(LocalDate.now()))
            .whereLessThanOrEqualTo("timein", endOfDay(LocalDate.now()))
            .get()
            .addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty){
                    callback(false)
                } else{
                    callback(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Fetch Data Failed","checkTapStatus $exception")
                callback(null)
            }
    }

    fun tapOutAttendance(db: FirebaseFirestore, user: User, attendance: Attendance,companyparams:CompanyParams) {
        val userref = db.collection("users").document(user.userid!!)
        val attendanceref = db.collection("attendances").document(attendance.attendanceid!!)
        val newmap = user.monthlytoleranceworktime!!
        val worktime = calcWorkTime(attendance.timein!!, curDateTime(),companyparams)
        if(worktime < companyparams.companyworktime!!){
            newmap[LocalDate.now().month.value.toString()] = newmap[LocalDate.now().month.value.toString()]!! + (worktime - companyparams.companyworktime!!)
        }
        db.runTransaction { transaction ->
            transaction.update(userref, "embedding", user.embedding,"monthlytoleranceworktime", newmap)
            transaction.update(attendanceref,"timeout", curDateTime(),"worktime",worktime)
            null
        }
            .addOnSuccessListener {
                Log.d("TAPOUT","Tap out attendance successful")
            }
            .addOnFailureListener {exception ->
                Log.e("TAPOUT","tapOutAttendance $exception")
            }

    }
    fun curDateTime(): Date{
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun startOfDay(date: LocalDate): Date{
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun endOfDay(date: LocalDate): Date{
        return Date.from(date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
    }
    fun firstDateOfMonth(date: LocalDate=LocalDate.now()):Date{
        return Date.from(date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun lastDateOfMonth(date: LocalDate=LocalDate.now()): Date{
        return Date.from(date.plusMonths(1).withDayOfMonth(1).minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
    }

    fun firstDateOfYear(date: LocalDate=LocalDate.now()): Date{
        return Date.from(date.with(firstDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun lastDateOfYear(date: LocalDate=LocalDate.now()): Date{
        return Date.from(date.with(lastDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun calcDurationDays(datestart: Date, dateend: Date): Int{
        return Duration.between(datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), dateend.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toDays().toInt() + 1
    }

    fun dateToLocalDate(date: Date): LocalDate{
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun localDateTimeToDate(date: LocalDateTime): Date{
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
    }

    fun calcWorkTime(timein: Date, timeout: Date, companyparams: CompanyParams): Int{
        val tapintime = companyparams.tapintime!!.split(":")
        val timeouttemp = timeout.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val timeintemp = timein.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        var targettime = LocalDateTime.of(timeouttemp.year,timeouttemp.month,timeouttemp.dayOfMonth,tapintime[0].toInt(),tapintime[1].toInt())
        targettime = targettime.plusMinutes((companyparams.companyworktime!! + companyparams.maxcompensatetime!!).toLong())
        if(timeouttemp.isAfter(targettime)){
            return Duration.between(timeintemp,targettime).toMinutes().toInt()
        }
        return Duration.between(timeintemp,timeouttemp).toMinutes().toInt()
    }

    fun companyTimeIn(date: LocalDateTime, companyparams: CompanyParams): Date{
        val tapintime = companyparams.tapintime!!.split(":")
        return localDateTimeToDate(date.withHour(tapintime[0].toInt()).withMinute(tapintime[1].toInt()))
    }

    fun companyTimeOut(date: LocalDateTime, companyparams: CompanyParams): Date{
        val tapouttime = companyparams.tapouttime!!.split(":")
        return localDateTimeToDate(date.withHour(tapouttime[0].toInt()).withMinute(tapouttime[1].toInt()))
    }
}