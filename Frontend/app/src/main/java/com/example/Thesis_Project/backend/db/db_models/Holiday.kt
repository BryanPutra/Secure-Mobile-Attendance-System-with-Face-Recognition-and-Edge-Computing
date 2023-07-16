package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class Holiday(
    val holidayid: String? = null,
    val holidayname: String? = null,
    var date: Date? = null
)
