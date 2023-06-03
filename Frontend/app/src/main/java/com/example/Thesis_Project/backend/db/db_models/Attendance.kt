package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class Attendance(
    var attendanceid: String? = null,
    val userid: String? = null,
    val leaveflag: Boolean? = null,
    val permissionflag: Boolean? = null,
    val absentflag: Boolean? = null,
    var timein: Date? = null,
    val timeout: Date? = null,
    val worktime: Int? = null,
    )
