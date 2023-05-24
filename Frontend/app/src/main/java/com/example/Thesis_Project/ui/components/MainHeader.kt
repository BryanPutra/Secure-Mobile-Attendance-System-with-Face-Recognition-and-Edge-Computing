package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme
import com.example.Thesis_Project.ui.utils.formatDateToString
import java.util.*

@Composable
fun historyNavButton(
    isSelected: MutableState<Boolean>,
    historyType: String,
    onClicked: () -> Unit
) {

    val textColor: Color = if (isSelected.value) {
        colorResource(id = R.color.blue_500)
    } else {
        colorResource(id = R.color.white)
    }

    val boxModifier: Modifier = if (isSelected.value) {
        Modifier
            .width(110.dp)
            .background(
                color = colorResource(id = R.color.white),
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = colorResource(id = R.color.blue_500)
                ),
                shape = RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge)
            )
            .padding(MaterialTheme.spacing.spaceSmall)
            .clickable { onClicked() }
    } else {
        Modifier
            .width(110.dp)
            .background(
                color = colorResource(id = R.color.blue_500),
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = colorResource(id = R.color.white),
                ),
                shape = RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge)
            )
            .padding(MaterialTheme.spacing.spaceSmall)
    }
    Box(
        modifier = boxModifier, contentAlignment = Alignment.Center
    ) {
        Text(text = historyType, color = textColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun MainHeader(page: String?, userFullName: String) {

    val correctionSelected = remember { mutableStateOf(false) };
    val leaveSelected = remember { mutableStateOf(false) };
    val currentDate = remember { mutableStateOf(Date()) }
    val currentDateString = formatDateToString(currentDate.value)

    val setCorrectionSelected = {
        correctionSelected.value = true;
        leaveSelected.value = false;
    }

    val setLeaveSelected = {
        correctionSelected.value = false;
        leaveSelected.value = true;
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .background(colorResource(id = R.color.blue_500))
            .padding(MaterialTheme.spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = userFullName,
                color = colorResource(id = R.color.gray_50),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = currentDateString,
                color = colorResource(id = R.color.gray_50),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (page !== BottomNavBarRoutes.HistoryScreen.route) {
            return
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
        ) {
            historyNavButton(
                isSelected = correctionSelected,
                historyType = "Correction",
                onClicked = setCorrectionSelected
            )
            historyNavButton(
                isSelected = leaveSelected,
                historyType = "Leave",
                onClicked = setLeaveSelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        MainHeader(
            "history_screen",
            "Bryan Putra",
        )
    }
}