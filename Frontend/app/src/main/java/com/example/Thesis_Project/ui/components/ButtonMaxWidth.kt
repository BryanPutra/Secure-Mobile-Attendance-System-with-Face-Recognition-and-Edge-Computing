package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.elevation
import com.example.secure_mobile_attendance_system_with_face_recognition_and_edge_computing.R

@Composable
fun ButtonMaxWidth(onClickCallback: () -> Unit, buttonText: String) {
    Button(
        onClick = {
            onClickCallback()
        },
        modifier = Modifier
            .fillMaxWidth(1f)
            .height(48.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = MaterialTheme.elevation.medium,
            pressedElevation = MaterialTheme.elevation.large,
            disabledElevation = MaterialTheme.elevation.default
        ),
        shape = RoundedCornerShape(20),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_500))
    ) {
        Text(text = buttonText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Normal)
    }
}