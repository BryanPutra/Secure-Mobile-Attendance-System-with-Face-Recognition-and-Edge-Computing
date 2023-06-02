package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class CorrectionRequest(
    val correctionrequestid: String? = null,
    val userid: String? = null,
    val timein: Date? = null,
    val timeout: Date? = null,
    val reason: String? = null,
    val approvedflag: Boolean? = null,
    val approvedtime: Date? = null,
    val approvedby: String? = null,
    val rejectedflag: Boolean? = null,
    val rejectedtime: Date? = null,
    val rejectedby: String? = null,
    )
