package com.example.Thesis_Project.backend.db.db_models
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LeaveRequest(
    var leaverequestid: String? = null,
    val userid: String? = null,
    val leavestart: Date? = null,
    val leaveend: Date? = null,
    var duration: Int? = null,
    val permissionflag: Boolean? = null,
    val reason: String? = null,
    val approvedflag: Boolean? = null,
    val approvedtime: Date? = null,
    val approvedby: String? = null,
    val rejectedflag: Boolean? = null,
    val rejectedtime: Date? = null,
    val rejectedby: String? = null,
    var createdate: Date? = null
    )
