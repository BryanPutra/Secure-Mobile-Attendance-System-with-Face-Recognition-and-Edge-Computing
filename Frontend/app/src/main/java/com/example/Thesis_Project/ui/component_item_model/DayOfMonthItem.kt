package com.example.Thesis_Project.ui.component_item_model

import com.example.Thesis_Project.backend.db.db_models.Attendance
import java.time.LocalDate

data class DayOfMonthItem(
    val date: LocalDate?,
    val dateString: String,
    val attendance: Attendance? = null,
    var isSelected: Boolean = false
)
