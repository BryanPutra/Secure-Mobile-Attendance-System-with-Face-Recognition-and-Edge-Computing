package com.example.Thesis_Project.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.Thesis_Project.ui.components.ButtonMaxWidth
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.ui.components.BottomNavigationBar
import com.example.Thesis_Project.ui.navgraphs.HomeNavGraph
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

enum class homeTapState {
    TAPPEDIN, TAPPEDOUTWORKHOUR, TAPPEDOUTNOTWORKHOUR,
}

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, onItemClicked = {})},
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                HomeNavGraph(
                    navController = navController,
                )
            }
        })
}

@Composable
fun TapCard(navController: NavController? = null, tapState: homeTapState) {

}

@Composable
fun TapInCard(tapInDisabled: Boolean) {

    val currentDateTime = rememberSaveable { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime.value = Date()
            delay(60000) // Delay for 1 minute (60000 milliseconds)
        }
    }

    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)
    val timeFormat = SimpleDateFormat("HH : mm", Locale.ENGLISH)
    val formattedDate = dateFormat.format(currentDateTime.value)
    val formattedTime = timeFormat.format(currentDateTime.value)

    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = colorResource(
                id = R.color.white
            )
        ), elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.spaceLarge,
                alignment = Alignment.CenterVertically
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.spaceSmall,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Today, contentDescription = null,
                    tint = colorResource(
                        id = R.color.blue_500
                    ),
                    modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                )
                Text(text = formattedDate, style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.headlineLarge,
                color = colorResource(id = R.color.blue_500)
            )
            ButtonMaxWidth(onClickCallback = { }, buttonText = "Tap In")
        }
    }
}

@Composable
fun TapOutCard() {

}

@Composable
fun NotesSection() {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val lastUpdatedDate by rememberSaveable { mutableStateOf(Date()) }
    var notesString by rememberSaveable {
        mutableStateOf("Meeting at 4PM \nDo Reports \nMeeting in 18th May")
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val formattedDate = dateFormat.format(lastUpdatedDate)

    val bulletPoint = "\u2022 "
    val annotatedText = buildAnnotatedString {
        val lines = notesString.trim().split("\n")

        lines.forEachIndexed { index, line ->
            append(bulletPoint)
            append(line)
            if (index < lines.size - 1) {
                append("\n")
            }
        }
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
            onClick = { isEditing = !isEditing },
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
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(1f),
                        value = notesString,
                        onValueChange = { newNotes -> notesString = newNotes })
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
                        text = "Last updated: $formattedDate",
                        color = colorResource(id = R.color.gray_700),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (isEditing) {
                        Button(
                            onClick = {},
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
fun HomeContainer(navController: NavController?) {

    val db: FirebaseFirestore = Firebase.firestore

    var user:User = User();
    db_util.getUser(db, "vMQz8RTu4iR7pJMLlrnN") { data ->
        if (data != null) {
            user = data;
            Log.e("USERDATA", user.email!!)
        } else {
            Log.e("USERDATA", "User not found")
        }
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
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = colorResource(
                            id = R.color.gray_50
                        ),
                        modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Logout,
                        contentDescription = null,
                        tint = colorResource(
                            id = R.color.gray_50
                        ),
                        modifier = Modifier.size(MaterialTheme.spacing.iconMedium)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray_50),
                    modifier = Modifier.size(MaterialTheme.spacing.iconExtraLarge)
                )
                user.userid?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            TapInCard(tapInDisabled = false)
            NotesSection()
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

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        HomeScreen()
//    }
//}