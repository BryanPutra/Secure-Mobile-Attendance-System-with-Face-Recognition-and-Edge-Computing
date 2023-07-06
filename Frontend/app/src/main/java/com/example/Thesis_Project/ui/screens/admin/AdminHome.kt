package com.example.Thesis_Project.ui.screens.admin

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.Holiday
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.*
import com.example.Thesis_Project.ui.navgraphs.AdminNavGraph
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.convertTimeMinutesIntToString
import com.example.Thesis_Project.ui.utils.formatDateToStringForInputs
import com.example.Thesis_Project.ui.utils.formatDateToStringWithOrdinal
import com.example.Thesis_Project.ui.utils.formatStringDateToDate
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun AdminHomeScreen(
    rootNavController: NavHostController,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel
) {
    if (mainViewModel.isLoading) {
        CircularLoadingBar()
    }
    Scaffold(
        bottomBar = { AdminBottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AdminNavGraph(
                    rootNavController = rootNavController,
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        })
}

@Composable
fun AdminHomeContainer(rootNavController: NavHostController, navController: NavController, mainViewModel: MainViewModel) {

    val maintainScope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val isLaunched by rememberSaveable { mutableStateOf(mainViewModel.isAdminHomeInit) }
    var logoutConfirmDialogShown by rememberSaveable { mutableStateOf(false) }
    var yearlyMaintenanceConfirmDialogShown by rememberSaveable { mutableStateOf(false) }
    var yearlyMaintenanceInfoDialogShown by rememberSaveable { mutableStateOf(false) }
    val info: String by remember { mutableStateOf("Reset users' tolerance work time\nAdd yearly leave to users\nReset fixed date holidays") }
    val bulletPoint = "\u2022 "
    val annotatedText by remember {
        mutableStateOf(
            buildAnnotatedString {
                val lines = info.trim().split("\n")
                lines.forEachIndexed { index, line ->
                    append(bulletPoint)
                    append(line)
                    if (index < lines.size - 1) {
                        append("\n")
                    }
                }
            }
        )
    }

    val yearlyMaintenanceHolidays: List<Holiday> = listOf(
        Holiday(holidayname = "New Year's Day", date = formatStringDateToDate("01/01/2023")),
        Holiday(holidayname = "Lunar New Year's Day", date = formatStringDateToDate("22/01/2023")),
        Holiday(holidayname = "Ascension of Prophet Muhammad", date = formatStringDateToDate("18/02/2023")),
        Holiday(holidayname = "Hindu New Year (Nyepi)", date = formatStringDateToDate("22/03/2023")),
        Holiday(holidayname = "Good Friday", date = formatStringDateToDate("07/04/2023")),
        Holiday(holidayname = "Idul Fitri", date = formatStringDateToDate("22/04/2023")),
        Holiday(holidayname = "Idul Fitri", date = formatStringDateToDate("23/04/2023")),
        Holiday(holidayname = "International Labor Day", date = formatStringDateToDate("01/05/2023")),
        Holiday(holidayname = "Ascension Day of Jesus Christ", date = formatStringDateToDate("18/05/2023")),
        Holiday(holidayname = "Pancasila Day", date = formatStringDateToDate("01/06/2023")),
        Holiday(holidayname = "Waisak Day (Buddha's Anniversary)", date = formatStringDateToDate("04/06/2023")),
        Holiday(holidayname = "Idul Adha", date = formatStringDateToDate("29/06/2023")),
        Holiday(holidayname = "Muharram / Islamic New Year", date = formatStringDateToDate("19/07/2023")),
        Holiday(holidayname = "Indonesian Independence Day", date = formatStringDateToDate("17/08/2023")),
        Holiday(holidayname = "Maulid Nabi Muhammad (The Prophet Muhammad's Birthday)", date = formatStringDateToDate("28/09/2023")),
        Holiday(holidayname = "Christmas Day", date = formatStringDateToDate("25/12/2023")),
        )

    suspend fun getInitData() {
        coroutineScope {
            launch{
                db_util.getUser(mainViewModel.db, mainViewModel.currentUser!!.uid, mainViewModel.setUserData)
            }
            launch {
                db_util.getCompanyParams(mainViewModel.db, mainViewModel.setCompanyVariable)
            }
        }
    }

    LaunchedEffect(key1 = isLaunched) {
        if (!isLaunched) {
            runBlocking {
                getInitData()
                mainViewModel.setIsAdminHomeInit(true)
            }
        }
    }

    val postYearlyMaintenance: suspend () -> Unit =
        {
            mainViewModel.setIsLoading(true)
            try {
                db_util.checkYearlyMaintenanceDone(
                    mainViewModel.db,
                ) { isDone ->
                    if (!isDone!!) {
                        db_util.adminYearlyMaintenance(
                            mainViewModel.db,
                            mainViewModel.companyVariable!!,
                            yearlyMaintenanceHolidays
                        ) { isSuccess ->
                            if (isSuccess!!) {
                                db_util.getHolidays(mainViewModel.db, null, mainViewModel.setHolidayList)
                                db_util.getAllUser(mainViewModel.db, mainViewModel.setUserList)
                                mainViewModel.showToast(context, "Yearly maintenance done successfully")
                            }
                            else{
                                mainViewModel.showToast(context, "Failed to complete the yearly maintenance")
                            }
                        }
                    } else {
                        mainViewModel.showToast(context, "Maintenance has been done this year")
                        Log.e("Error", "Maintenance has been done this year")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(context, "Failed to complete the yearly maintenance: $e")
                Log.e("Error", "Failed to complete the yearly maintenance: $e")
            }
            yearlyMaintenanceConfirmDialogShown = false
            mainViewModel.setIsLoading(false)
        }

    fun onMaintainClicked(){
        yearlyMaintenanceConfirmDialogShown = true
    }

    fun onConfirmMaintainClicked(){
        maintainScope.launch {
            postYearlyMaintenance()
        }
    }

    if (yearlyMaintenanceConfirmDialogShown){
        AlertDialog(
            onDismissRequest = {
                yearlyMaintenanceConfirmDialogShown = false
            },
            title = { Text(text = "Yearly Maintenance") },
            text = { Text(text = "Are you sure you want to:\n$annotatedText") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmMaintainClicked()
                        yearlyMaintenanceConfirmDialogShown = false
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        yearlyMaintenanceConfirmDialogShown = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
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
                        onClick = { mainViewModel.toggleIsEditCompanyParamsDialogShown() },
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
                        name = "Tolerance work time", value = convertTimeMinutesIntToString(
                            mainViewModel.companyVariable?.toleranceworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Company work time", value = convertTimeMinutesIntToString(
                            mainViewModel.companyVariable?.companyworktime
                        )
                    )
                    CompanyQuotasRow(
                        name = "Compensation Work Time", value = convertTimeMinutesIntToString(
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
            Column(
                modifier = Modifier
                    .fillMaxWidth().wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Yearly Maintenance",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Icon(
                        modifier = Modifier.size(MaterialTheme.spacing.iconMedium).clickable {
                            yearlyMaintenanceInfoDialogShown = true
                        },
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colorResource(id = R.color.blue_500)
                    )
                }
                ButtonHalfWidth(
                    onClick = { onMaintainClicked() },
                    buttonText = "Maintain"
                )
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

        if (yearlyMaintenanceInfoDialogShown){
            AdminYearlyMaintenanceInfoDialog {
                yearlyMaintenanceInfoDialogShown = false
            }
        }


        if (logoutConfirmDialogShown) {
            AlertDialog(
                onDismissRequest = { logoutConfirmDialogShown = false },
                title = { Text(text = "Logout") },
                text = { Text(text = "Are you sure you want to log out?") },
                confirmButton = {
                    Button(
                        onClick = {
                            runBlocking {
                                mainViewModel.setIsLoading(true)
                                logoutConfirmDialogShown = false
                                mainViewModel.signOutFromAdmin()
                                navController.popBackStack()
                                rootNavController.navigate(NavGraphs.ROOT) {
                                    popUpTo(NavGraphs.ADMIN) { inclusive = true}
                                }
                                mainViewModel.setIsLoading(false)
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