package com.example.Thesis_Project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.formatDateToStringWithDay
import com.example.Thesis_Project.viewmodel.MainViewModel
import java.util.*

@Composable
fun HistoryNavButton(
    isSelected: Boolean,
    historyType: String,
    onClicked: () -> Unit,
    isAdminApprovePage: Boolean = false
) {

    val buttonColor: Color = if (isAdminApprovePage) {
        if (isSelected) {
            colorResource(id = R.color.white)
        } else {
            colorResource(id = R.color.blue_500)
        }
    } else {
        if (isSelected) {
            colorResource(id = R.color.blue_500)
        } else {
            colorResource(id = R.color.white)
        }
    }

    val inverseButtonColor: Color = if (isAdminApprovePage) {
        if (isSelected) {
            colorResource(id = R.color.blue_500)
        } else {
            colorResource(id = R.color.white)
        }
    } else {
        if (isSelected) {
            colorResource(id = R.color.white)
        } else {
            colorResource(id = R.color.blue_500)
        }
    }

    val boxModifier: Modifier =
        Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge))
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = buttonColor
                ),
                shape = RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge)
            )
            .background(
                color = inverseButtonColor,
            )
            .padding(MaterialTheme.spacing.spaceSmall)
            .clickable { onClicked() }

    val boxModifierAdminPage: Modifier = Modifier
        .width(130.dp)
        .height(50.dp)
        .clip(RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge))
        .border(
            border = BorderStroke(
                width = 1.dp,
                color = buttonColor
            ),
            shape = RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge)
        )
        .background(
            color = inverseButtonColor,
        )
        .padding(MaterialTheme.spacing.spaceSmall)
        .clickable { onClicked() }

    Box(
        modifier = if (isAdminApprovePage) {
            boxModifierAdminPage
        } else {
            boxModifier
        }, contentAlignment = Alignment.Center
    ) {
        Text(text = historyType, color = buttonColor, style = if(isAdminApprovePage) {MaterialTheme.typography.bodyLarge} else {MaterialTheme.typography.bodySmall} )
    }
}

@Composable
fun MainHeader(
    page: String?,
    userFullName: String?,
    correctionSelected: Boolean? = null,
    leaveSelected: Boolean? = null,
    mainViewModel: MainViewModel
) {

    val currentDate by remember { mutableStateOf(Date()) }
    val currentDateString =
        if (page == BottomNavBarRoutes.HistoryScreen.route) formatDateToStringWithDay(currentDate) else "Leave Left: ${mainViewModel.userData?.leaveleft ?: 0} Days"

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
                text = userFullName ?: "",
                color = colorResource(id = R.color.gray_50),
                style = MaterialTheme.typography.bodyLarge
            )
            if (currentDateString != null) {
                Text(
                    text = currentDateString,
                    color = colorResource(id = R.color.gray_50),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (page == BottomNavBarRoutes.HistoryScreen.route) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)
            ) {
                HistoryNavButton(
                    isSelected = correctionSelected ?: false,
                    historyType = "Correction",
                    onClicked = mainViewModel.onCorrectionTabClicked
                )
                HistoryNavButton(
                    isSelected = leaveSelected ?: false,
                    historyType = "Leave",
                    onClicked = mainViewModel.onLeaveTabClicked
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        MainHeader(
//            "history_screen",
//            "Bryan Putra",
//        )
//    }
//}