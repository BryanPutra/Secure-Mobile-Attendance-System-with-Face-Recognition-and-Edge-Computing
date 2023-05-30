package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class User(
    val userid: String? = null,
    val email: String? = null,
    val password: String? = null,
    val adminflag: Boolean? = null,
    val note: String? = null,
    val lastupdated: Date? = null,
    val embedding: String? = null
    )
