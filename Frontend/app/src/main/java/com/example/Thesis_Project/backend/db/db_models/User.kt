package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class User(
    val userid: String? = null,
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val adminflag: Boolean? = null,
    val note: String? = null,
    val notelastupdated: Date? = null,
    val embedding: String? = null,
    val leaveleft: Int? = null,
    val leaveallow: Boolean? = null,
    val permissionleft: Int? = null,
    val monthlytoleranceworktime: Map<String,Int>? = null,
    val joindate: Date? = null
    )
