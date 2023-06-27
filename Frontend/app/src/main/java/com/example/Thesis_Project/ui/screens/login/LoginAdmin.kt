package com.example.Thesis_Project.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.ButtonMaxWidth
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.routes.AuthScreenRoutes
import com.example.Thesis_Project.ui.components.CircularLoadingBar
import com.example.Thesis_Project.ui.navgraphs.NavGraphs
import com.example.Thesis_Project.ui.utils.isValidEmail
import com.example.Thesis_Project.ui.utils.isValidPassword
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginAdminScreen(navController: NavController, mainViewModel: MainViewModel) {
    LoginAdminContainer(navController, mainViewModel)
}

@Composable
fun LoginAdminContainer(navController: NavController, mainViewModel: MainViewModel) {
    if (mainViewModel.isLoading) {
        CircularLoadingBar()
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(id = R.dimen.padding_xl)),
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceLarge,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginAdminHeader()
        LoginAdminInputs(navController, mainViewModel)
    }
}

@Composable
fun LoginAdminHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceLarge,
            alignment = Alignment.CenterVertically
        ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.admin),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            "Login as Admin",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun LoginAdminInputs(navController: NavController, mainViewModel: MainViewModel) {

    val loginAdminScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordIsVisible by remember { mutableStateOf(false) }
    var emailIsValid by remember { mutableStateOf(true) }
    var passwordIsValid by remember { mutableStateOf(true) }

    var errorText by remember { mutableStateOf("") }

    val postSignIn: suspend () -> Unit = {
        mainViewModel.setIsLoading(true)
        mainViewModel.signIn(email, password, {
            db_util.checkUserIsAdmin(
                mainViewModel.db,
                mainViewModel.currentUser!!.uid
            ) { isAdmin ->
                mainViewModel.setUserAdmin(isAdmin)
                mainViewModel.setIsLoggedInAsAdmin(isAdmin)
                Log.e("check logged in as admin 3", "${mainViewModel.isLoggedInAsAdmin} ${mainViewModel.isUserAdmin}")
                if (mainViewModel.isUserAdmin) {
                    navController.navigate(NavGraphs.ADMIN) {
                        popUpTo(NavGraphs.ROOT) { saveState = true }
                    }
                    mainViewModel.showToast(context, "Logged in as Admin")
                } else {
                    mainViewModel.signOutFromUser()
                    errorText =
                        "The inputted user is a user, please login as user in the user login page"
                }
                mainViewModel.setIsLoading(false)
            }
        }, { errorMessage -> errorText = errorMessage })
        mainViewModel.setIsLoading(false)
    }

    fun onSubmitLogin() {
        emailIsValid = isValidEmail(email)
        passwordIsValid = isValidPassword(password)

        if (!emailIsValid) {
            errorText = "Please enter a valid email"
            return
        }

        if (!passwordIsValid) {
            errorText = "Password needs to be at least 6 characters"
            return
        }
        loginAdminScope.launch {
            postSignIn()
        }
    }

    Column(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.spaceLarge),
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
                value = email,
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = !emailIsValid,
                onValueChange = { text -> email = text },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f),
                value = password,
                onValueChange = { text -> password = text },
                label = { Text(text = "Password") },
                visualTransformation = if (passwordIsVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = !passwordIsValid,
                trailingIcon = {
                    val icon = if (passwordIsVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    IconButton(
                        onClick = {
                            passwordIsVisible = !passwordIsVisible
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                }
            )
            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
        ButtonMaxWidth(onClickCallback = {
            onSubmitLogin()
        }, buttonText = "Login")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        LoginAdminScreen()
//    }
//}