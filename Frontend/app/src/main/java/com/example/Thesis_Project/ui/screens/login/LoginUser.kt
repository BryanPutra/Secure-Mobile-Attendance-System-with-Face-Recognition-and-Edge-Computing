package com.example.Thesis_Project.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.ButtonMaxWidth
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.BottomNavBarRoutes

@Composable
fun LoginUserScreen(navController: NavController) {
    LoginUserContainer(navController)
}

@Composable
fun LoginUserContainer(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(dimensionResource(id = R.dimen.padding_xl)),
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceLarge,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginUserHeader()
        LoginUserInputs(navController)
    }
}

@Composable
fun LoginUserHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceLarge,
            alignment = Alignment.CenterVertically
        ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            "Login",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun LoginUserInputs(navController: NavController) {

    fun onSubmitLogin(){
        navController.navigate(BottomNavBarRoutes.HomeScreen.route)
    }

    var username by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceLarge,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.spaceLarge,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f),
                value = username,
                label = { Text(text = "Username") },
                onValueChange = { text -> username = text },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f),
                value = password,
                onValueChange = { text -> password = text },
                label = { Text(text = "Password") },
            )
        }
        ButtonMaxWidth(onClickCallback = {onSubmitLogin()}, buttonText = "Login")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        LoginUserScreen()
//    }
//}