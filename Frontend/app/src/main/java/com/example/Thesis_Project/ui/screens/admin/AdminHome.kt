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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.Thesis_Project.routes.AdminBottomNavBarRoutes
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.AdminBottomNavigationBar
import com.example.Thesis_Project.ui.components.AdminEditCompanyParamsDialog
import com.example.Thesis_Project.ui.components.ButtonHalfWidth
import com.example.Thesis_Project.ui.components.CompanyQuotasRow
import com.example.Thesis_Project.ui.navgraphs.AdminNavGraph
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.convertTimeIntToString
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminHomeScreen(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel
) {
    Scaffold(
        bottomBar = { AdminBottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AdminNavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        })
}

@Composable
fun AdminHomeContainer(navController: NavController, mainViewModel: MainViewModel) {

    var logoutConfirmDialogShown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db_util.getUser(mainViewModel.db, mainViewModel.currentUser!!.uid) { data ->
            if (data != null) {
                mainViewModel.userData = data;
                Log.d("USERADMINDATA", mainViewModel.userData!!.userid!!);
            } else {
                Log.e("USERADMINDATA", "Admin not found")
            }
        }
        db_util.getCompanyParams(mainViewModel.db, mainViewModel.setCompanyVariable)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Profile",
                    color = colorResource(id = R.color.gray_50),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null,
                    tint = colorResource(
                        id = R.color.gray_50
                    ),
                    modifier = Modifier
                        .size(MaterialTheme.spacing.iconMedium)
                        .clickable {
                            logoutConfirmDialogShown = true
                        }
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
                        text = mainViewModel.userData?.name ?: "",
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
                            text = mainViewModel.userData?.email ?: "",
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
                            text = formatDateToStringWithOrdinal(mainViewModel.userData?.joindate) ?: "",
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
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Company Quotas",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    ButtonHalfWidth(
                        onClick = { mainViewModel.showEditCompanyParamsDialog() },
                        buttonText = "Edit"
                    )
                }
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
                        value = "${mainViewModel.companyVariable?.maxtotalleaveleft} Days"
                    )
                    CompanyQuotasRow(
                        name = "Leave per year",
                        value = "${mainViewModel.companyVariable?.leaveleft} Days"
                    )
                    CompanyQuotasRow(
                        name = "Leave usage per month",
                        value = "${mainViewModel.companyVariable?.maxmonthlyleaveleft ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Minimum days for leave",
                        value = "${mainViewModel.companyVariable?.minimumdaysworked ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Permission per year",
                        value = "${mainViewModel.companyVariable?.maxpermissionsleft ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Company tap in time",
                        value = "${mainViewModel.companyVariable?.tapintime ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Company tap out time",
                        value = "${mainViewModel.companyVariable?.tapouttime ?: 0} Days"
                    )
                    CompanyQuotasRow(
                        name = "Tolerance work time", value = convertTimeIntToString(
                            mainViewModel.companyVariable?.toleranceworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Company work time", value = convertTimeIntToString(
                            mainViewModel.companyVariable?.companyworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Compensate work time", value = convertTimeIntToString(
                            mainViewModel.companyVariable?.maxcompensatetime
                        )
                    )
                    mainViewModel.companyVariable?.wifissid?.let {
                        CompanyQuotasRow(
                            name = "Wifi SSID", value = it, isUnderlined = false
                        )
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

        if (mainViewModel.isEditCompanyParamsDialogShown) {
            AdminEditCompanyParamsDialog(mainViewModel = mainViewModel)
        }

        if (logoutConfirmDialogShown) {
            AlertDialog(
                onDismissRequest = { logoutConfirmDialogShown = false },
                title = { Text(text = "Logout") },
                text = { Text(text = "Are you sure you want to log out?") },
                confirmButton = {
                    Button(
                        onClick = {
                            logoutConfirmDialogShown = false
                            mainViewModel.signOutFromAdmin()
                            navController.navigate(NavGraphs.ROOT) {
                                popUpTo(AdminBottomNavBarRoutes.AdminHomeScreen.route) { inclusive = true }
                            }
                        }
                    ) {
                        Text(text = "Logout")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { logoutConfirmDialogShown = false }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
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