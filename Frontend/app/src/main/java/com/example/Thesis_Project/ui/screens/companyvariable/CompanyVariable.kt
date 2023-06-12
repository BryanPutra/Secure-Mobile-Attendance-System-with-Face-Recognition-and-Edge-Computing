package com.example.Thesis_Project.ui.screens.companyvariable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.Thesis_Project.R
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.CompanyQuotasRow
import com.example.Thesis_Project.ui.theme.SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme

@Composable
fun CompanyVariable() {
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(MaterialTheme.spacing.spaceLarge),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.zIndex(2f),
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.spaceLarge,
                alignment = Alignment.CenterVertically
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.spaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = colorResource(
                        id = R.color.gray_50
                    ),
                    modifier = Modifier.size(MaterialTheme.spacing.iconLarge)
                )
                Text(
                    text = "Profile",
                    color = colorResource(id = R.color.gray_50),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.spaceMedium)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray_50),
                    modifier = Modifier.size(MaterialTheme.spacing.iconExtraLarge)
                )
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceExtraSmall)) {
                    Text(
                        text = "Bently Edyson",
                        color = colorResource(id = R.color.gray_50),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = colorResource(id = R.color.gray_50),
                            modifier = Modifier.size(MaterialTheme.spacing.iconSmall)
                        )
                        Text(
                            text = "bentlyedyson@gmail.com",
                            color = colorResource(id = R.color.gray_50),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = colorResource(id = R.color.gray_50),
                            modifier = Modifier.size(MaterialTheme.spacing.iconSmall)
                        )
                        Text(
                            text = "Joined at June 19th 2022",
                            color = colorResource(id = R.color.gray_50),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.spaceXXLarge)) {
                Text(
                    "Company Quotas",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(
                        id = R.color.white
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
            ) {
                Column(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.spaceMedium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = MaterialTheme.spacing.spaceMedium,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    CompanyQuotasRow(name = "Leave left", value = "10 Days")
                    CompanyQuotasRow(name = "Permission left", value = "12 Days")
                    CompanyQuotasRow(name = "Tolerance work time ", value = "6 Hours 30 Minutes")
                    CompanyQuotasRow(name = "Attended this month", value = "4 Days", isUnderlined = false)
                }
            }
        }
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(225.dp)
                .align(Alignment.TopCenter)
                .offset(x = 0.dp, y = (-110).dp)
                .graphicsLayer {
                    this.scaleX = 2f
                    this.scaleY = 2f
                }
                .background(
                    colorResource(id = R.color.blue_500),
                    RoundedCornerShape(MaterialTheme.spacing.borderRadiusXXLarge)
                )
                .zIndex(-100f)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CompanyVariable()
        }
    }
}