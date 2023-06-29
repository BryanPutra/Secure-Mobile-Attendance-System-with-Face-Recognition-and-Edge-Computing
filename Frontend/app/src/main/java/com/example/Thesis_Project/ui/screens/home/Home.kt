package com.example.Thesis_Project.ui.screens.home

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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.R
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.COMPANYVAR_KEY
import com.example.Thesis_Project.SharedPreferencesConstants.Companion.PREFERENCES
import com.example.Thesis_Project.backend.camera.Model
import com.example.Thesis_Project.backend.db.db_models.CompanyParams
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.routes.BottomNavBarRoutes
import com.example.Thesis_Project.routes.HomeSubGraphRoutes
import com.example.Thesis_Project.ui.components.*
import com.example.Thesis_Project.ui.navgraphs.HomeNavGraph
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.formatDateToString
import com.example.Thesis_Project.viewmodel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

enum class homeTapState {
    TAPPEDIN, TAPPEDOUTWORKHOUR, TAPPEDOUTNOTWORKHOUR,
}

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel
) {
    var currentScreen by rememberSaveable { mutableStateOf<String?>(null) }
    if (mainViewModel.isLoading) {
        CircularLoadingBar()
    }
    Scaffold(
        bottomBar = {
            when (currentScreen) {
                BottomNavBarRoutes.HomeScreen.route,
                BottomNavBarRoutes.CalendarScreen.route,
                BottomNavBarRoutes.HistoryScreen.route -> {
                    BottomNavigationBar(navController = navController)
                }
            }
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                HomeNavGraph(
                    rootNavController = rootNavController,
                    navController = navController,
                    mainViewModel = mainViewModel,
                    onScreenChanged = { screen ->
                        currentScreen = screen
                    }
                )
            }
        })
}

@Composable
fun NotesSection(mainViewModel: MainViewModel) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val notesValue by rememberSaveable { mutableStateOf(mainViewModel.userData?.note) }

    val bulletPoint = "\u2022 "
    val annotatedText = buildAnnotatedString {
        val lines = mainViewModel.userData?.note?.trim()?.split("\n")
        lines?.forEachIndexed { index, line ->
            append(bulletPoint)
            append(line)
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }

    fun switchEditing() {
        isEditing = !isEditing
    }

    fun submitNote() {

    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Notes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Card(
            onClick = { switchEditing() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(
                    id = R.color.light_blue_50
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceLarge,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                if (isEditing) {
                    notesValue?.let {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            value = it,
                            onValueChange = { newNotes -> mainViewModel.userData?.note = newNotes })
                    }
                } else {
                    Text(
                        text = annotatedText,
                        color = colorResource(id = R.color.black)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(colorResource(id = R.color.blue_500))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last updated: ${formatDateToString(mainViewModel.userData?.notelastupdated)}",
                        color = colorResource(id = R.color.gray_700),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (isEditing) {
                        Button(
                            onClick = { switchEditing() },
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = MaterialTheme.elevation.medium,
                                pressedElevation = MaterialTheme.elevation.large,
                                disabledElevation = MaterialTheme.elevation.default
                            ),
                            shape = RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_500))
                        ) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContainer(
    rootNavController: NavHostController,
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context: Context = LocalContext.current
    var logoutConfirmDialogShown by rememberSaveable { mutableStateOf(false) }
    val initHomeScope = rememberCoroutineScope()
    val sharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    val gson = Gson()

    suspend fun getInitData() {
        mainViewModel.setIsLoading(true)
        coroutineScope {
            launch {
                db_util.getUser(
                    mainViewModel.db,
                    mainViewModel.currentUser!!.uid,
                ) { user ->
                    mainViewModel.setUserData(user)
                }
                Log.e(
                    "getuserdataoninit",
                    "${mainViewModel.userData}" + "${mainViewModel.currentUser}"
                )
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.userData!!.userid,
                    db_util.firstDateOfMonth(),
                    db_util.lastDateOfMonth(),
                ) { attendances ->
                    if (attendances == null) {
                        mainViewModel.setAttendanceList(null)
                    } else {
                        mainViewModel.setAttendanceList(attendances)
                    }
                }
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.userData!!.userid,
                    db_util.startOfDay(LocalDate.now()),
                    db_util.endOfDay(LocalDate.now()),
                ) { attendances ->
                    if (attendances == null) {
                        mainViewModel.setTodayAttendance(null)
                    } else {
                        mainViewModel.setTodayAttendance(attendances[0])
                    }
                }
                if (mainViewModel.todayAttendance == null) {
                    Log.d("todayattendancenotnull", "settapindisabled")
                    mainViewModel.setTapInDisabled(false)
                } else {
                    if (mainViewModel.todayAttendance!!.timeout == null) {
                        mainViewModel.setIsTappedIn(true)
                    } else {
                        mainViewModel.setTapInDisabled(true)
                    }
                }
                if (mainViewModel.userData!!.embedding != null) {
                    //overwrite the embeddings di local with the one from database in case
                    // clear data in app
                    context.openFileOutput(Model.fileName, Context.MODE_PRIVATE).use {
                        it.write(mainViewModel.userData!!.embedding?.toByteArray())
                    }
                    mainViewModel.setIsFaceRegistered(true)
                    return@launch
                }
                //kalau check local lagi nanti ktemunya muka orang lain because reset embedding on registerface
                mainViewModel.setIsFaceRegistered(false)
            }
            launch {
                if (sharedPreferences.contains(COMPANYVAR_KEY)) {
                    val companyParamsString = sharedPreferences.getString(COMPANYVAR_KEY, null)
                    val companyParams =
                        gson.fromJson(companyParamsString, CompanyParams::class.java)
                    mainViewModel.setCompanyVariable(companyParams)
                    return@launch
                }
                db_util.getCompanyParams(mainViewModel.db) { companyVariables ->
                    mainViewModel.setCompanyVariable(companyVariables)
                }
                val companyParamString = gson.toJson(mainViewModel.companyVariable)
                val editor = sharedPreferences.edit()
                editor.putString(COMPANYVAR_KEY, companyParamString)
                editor.apply()
            }
        }
    }
    LaunchedEffect(Unit) {
        if (!mainViewModel.isHomeInit) {
            runBlocking {
                getInitData()
                mainViewModel.setIsHomeInit(true)
                mainViewModel.setIsLoading(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (mainViewModel.isHomeInit) {
            initHomeScope.launch {
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.userData!!.userid,
                    db_util.firstDateOfMonth(),
                    db_util.lastDateOfMonth(),
                ) { attendances ->
                    if (attendances == null) {
                        mainViewModel.setAttendanceList(null)
                    } else {
                        mainViewModel.setAttendanceList(attendances)
                    }
                }
                db_util.getAttendance(
                    mainViewModel.db,
                    mainViewModel.currentUser?.uid,
                    db_util.startOfDay(LocalDate.now()),
                    db_util.endOfDay(LocalDate.now()),
                ) { attendances ->
                    if (attendances == null) {
                        mainViewModel.setTodayAttendance(null)
                    } else {
                        mainViewModel.setTodayAttendance(attendances[0])
                    }
                }
                if (mainViewModel.todayAttendance == null) {
                    Log.d("todayattendancenotnull", "settapindisabled")
                    mainViewModel.setTapInDisabled(false)
                } else {
                    if (mainViewModel.todayAttendance!!.timeout == null) {
                        mainViewModel.setIsTappedIn(true)
                    } else {
                        mainViewModel.setTapInDisabled(true)
                    }
                }
            }
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
                            val editor = sharedPreferences.edit()
                            editor.remove(COMPANYVAR_KEY)
                            editor.apply()
                            logoutConfirmDialogShown = false
                            mainViewModel.signOutFromUser()
                            navController.popBackStack()
                            rootNavController.navigate(NavGraphs.ROOT) {
                                popUpTo(NavGraphs.HOME) { inclusive = true }
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

    if (!mainViewModel.isHomeInit) {
        CircularLoadingBar()
    } else {
        if (!mainViewModel.isFaceRegistered) {
            RegisterFaceDialog(mainViewModel = mainViewModel, navController = navController)
        }

        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(MaterialTheme.spacing.spaceLarge),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier.zIndex(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceLarge,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = MaterialTheme.spacing.spaceLarge,
                        alignment = Alignment.CenterVertically
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = MaterialTheme.spacing.spaceLarge,
                            alignment = Alignment.End
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = colorResource(
                                id = R.color.gray_50
                            ),
                            modifier = Modifier
                                .size(MaterialTheme.spacing.iconMedium)
                                .clickable {
                                    navController.navigate(HomeSubGraphRoutes.CompanyVarScreen.route)
                                }
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
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = colorResource(id = R.color.gray_50),
                        modifier = Modifier.size(MaterialTheme.spacing.iconXXLarge)
                    )
                    Text(
                        text = mainViewModel.userData?.name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = colorResource(id = R.color.gray_50),
                        fontWeight = FontWeight.Normal
                    )
                }
                if (mainViewModel.isHomeInit) {
                    if (mainViewModel.isTappedIn) {
                        TapOutCard(navController = navController, mainViewModel = mainViewModel)
                    } else {
                        TapInCard(navController = navController, mainViewModel = mainViewModel)
                    }
                }
                NotesSection(mainViewModel)
            }
            Box(
                modifier = Modifier
                    .width(231.dp)
                    .height(225.dp)
                    .graphicsLayer {
                        this.scaleX = 2f
                        this.scaleY = 2f
                    }
                    .background(
                        colorResource(id = R.color.blue_500),
                        RoundedCornerShape(MaterialTheme.spacing.borderRadiusExtraLarge)
                    )
                    .offset(x = 0.dp, y = 0.dp)
                    .zIndex(-100f)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        HomeScreen()
//    }
//}