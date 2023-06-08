package com.example.Thesis_Project.backend.db.db_models
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CorrectionRequest(
    var correctionrequestid: String? = null,
    val userid: String? = null,
    val attendanceid: String? = null,
    val timein: Date? = null,
    val timeout: Date? = null,
    val reason: String? = null,
    val leaveflag: Boolean? = null,
    val permissionflag: Boolean? = null,
    val presentflag: Boolean? = null,
    val approvedflag: Boolean? = null,
    val approvedtime: Date? = null,
    val approvedby: String? = null,
    val rejectedflag: Boolean? = null,
    val rejectedtime: Date? = null,
    val rejectedby: String? = null,
    var createdate: Date? = null
    )
