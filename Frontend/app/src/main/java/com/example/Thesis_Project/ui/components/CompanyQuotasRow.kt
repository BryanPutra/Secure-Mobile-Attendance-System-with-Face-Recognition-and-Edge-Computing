package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.R

@Composable
fun CompanyQuotasRow(name: String, value: String, isUnderlined: Boolean = true){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = name, color = colorResource(id = R.color.black))
        Text(text = value, color = colorResource(id = R.color.blue_500))
    }
    if (isUnderlined){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(id = R.color.gray_400))
        )
    }
}