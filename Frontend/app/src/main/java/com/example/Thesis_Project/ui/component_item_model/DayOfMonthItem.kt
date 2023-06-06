package com.example.Thesis_Project.ui.component_item_model

import com.example.Thesis_Project.backend.db.db_models.Attendance
import java.util.*

data class DayOfMonthItem(
    val dateString: String,
    val date: Date,
    val attendance: Attendance
)
