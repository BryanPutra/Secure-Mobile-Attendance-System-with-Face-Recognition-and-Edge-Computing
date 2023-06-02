package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class LeaveRequest(
    val leaverequestid: String? = null,
    val userid: String? = null,
    val leavestart: Date? = null,
    val leaveend: Date? = null,
    val permissionflag: Boolean? = null,
    val reason: String? = null,
    val approvedflag: Boolean? = null,
    val approvedtime: Date? = null,
    val approvedby: String? = null,
    val rejectedflag: Boolean? = null,
    val rejectedtime: Date? = null,
    val rejectedby: String? = null,
    )
