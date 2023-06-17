package com.example.Thesis_Project.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.AdminBottomNavigationBar
import com.example.Thesis_Project.ui.components.CompanyQuotasRow
import com.example.Thesis_Project.ui.navgraphs.AdminNavGraph
import com.example.Thesis_Project.ui.utils.convertTimeIntToString
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.AdminViewModel
import java.time.LocalDate

@Composable
fun AdminHomeScreen(
    navController: NavHostController = rememberNavController(),
    adminViewModel: AdminViewModel
) {
    Scaffold(
        bottomBar = { AdminBottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AdminNavGraph(
                    navController = navController,
                    adminViewModel = adminViewModel
                )
            }
        })
    AdminHomeContainer(navController, adminViewModel)
}

@Composable
fun AdminHomeContainer(navController: NavController, adminViewModel: AdminViewModel) {

    val currentMonthInt = LocalDate.now().monthValue

    LaunchedEffect(Unit) {
        db_util.getUser(adminViewModel.db, adminViewModel.currentUser!!.uid) { data ->
            if (data != null) {
                adminViewModel.userData = data;
                Log.d("USERADMINDATA", adminViewModel.userData!!.userid!!);
            } else {
                Log.e("USERADMINDATA", "Admin not found")
            }
        }
        db_util.getCompanyParams(adminViewModel.db, adminViewModel.setCompanyVariable)
    }

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
                    modifier = Modifier
                        .size(MaterialTheme.spacing.iconLarge)
                        .clickable { navController.popBackStack() },
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
                        text = adminViewModel.userData?.name ?: "",
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
                            text = adminViewModel.userData?.email ?: "",
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
                            text = formatDateToStringWithOrdinal(adminViewModel.userData?.joindate) ?: "",
                            color = colorResource(id = R.color.gray_50),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.spaceXXLarge)
            ) {
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
                    CompanyQuotasRow(
                        name = "Total max leave per user",
                        value = "${adminViewModel.companyVariable?.maxtotalleaveleft} Days"
                    )
                    CompanyQuotasRow(
                        name = "Leave per year",
                        value = "${adminViewModel.companyVariable?.leaveleft} Days"
                    )
                    CompanyQuotasRow(
                        name = "Leave usage per month",
                        value = "${adminViewModel.companyVariable?.maxmonthlyleaveleft ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Minimum days for leave",
                        value = "${adminViewModel.companyVariable?.minimumdaysworked ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Permission per year",
                        value = "${adminViewModel.companyVariable?.maxpermissionsleft ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Company tap in time",
                        value = "${adminViewModel.companyVariable?.tapintime ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Company tap out time",
                        value = "${adminViewModel.companyVariable?.tapouttime ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Tolerance work time", value = convertTimeIntToString(
                            adminViewModel.companyVariable?.toleranceworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Company work time", value = convertTimeIntToString(
                            adminViewModel.companyVariable?.companyworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Compensate work time", value = convertTimeIntToString(
                            adminViewModel.companyVariable?.maxcompensatetime
                        )
                    )
                    adminViewModel.companyVariable?.wifissid?.let {
                        CompanyQuotasRow(
                            name = "Wifi SSID", value = it, isUnderlined = false)
                    }
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

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        Box(modifier = Modifier.fillMaxSize()) {
//            AdminHome()
//        }
//    }
//}