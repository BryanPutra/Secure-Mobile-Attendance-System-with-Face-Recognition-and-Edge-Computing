package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class User(
    val userid: String? = null,
    val email: String? = null,
    val name: String? = null,
    val adminflag: Boolean? = null,
    var note: String? = null,
    var notelastupdated: Date? = null,
    val embedding: String? = null,
    var leaveleft: Int? = null,
    var leaveallow: Boolean? = null,
    var monthlytoleranceworktime: MutableMap<String,Int>? = null,
    var joindate: Date? = null
    )
