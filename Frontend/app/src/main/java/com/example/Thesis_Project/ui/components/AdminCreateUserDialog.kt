package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@Composable
fun AdminCreateUserDialog(mainViewModel: MainViewModel) {

    val createUserScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var adminFlag by rememberSaveable { mutableStateOf(false) }

    var nameIsValid by remember { mutableStateOf(true) }
    var emailIsValid by remember { mutableStateOf(true) }
    var passwordIsValid by remember { mutableStateOf(true) }

    var errorText by remember { mutableStateOf("") }

    val postCreateUser: suspend (user: User) -> Unit = { user ->
        mainViewModel.setIsLoading(true)
        mainViewModel.companyVariable?.let {
            mainViewModel.setIsLoading(true)
            try {
                db_util.createUserAuth(
                    mainViewModel.createUserAuth,
                    mainViewModel.db,
                    user,
                    email,
                    password,
                    it
                )
                db_util.getAllUser(mainViewModel.db, mainViewModel.setUserList)
                errorText = ""
                mainViewModel.showToast(context, "User has been created successfully")
                mainViewModel.toggleCreateUserDialog()
            } catch (e: Exception) {
                errorText = "Failed to create user: ${e.message}"
                Log.e("Error", "Failed to create user: $e")
            }
            Log.d("shit 1", "Create user done")
        }
        mainViewModel.setIsLoading(false)
    }

    fun onSubmitClicked() {
        nameIsValid = isValidName(name)
        emailIsValid = isValidEmail(email)
        passwordIsValid = isValidPassword(password)

        if (!nameIsValid) {
            errorText = "Name needs to be at least 3 characters"
            return
        }

        if (!emailIsValid) {
            errorText = "Please enter a valid email"
            return
        }

        if (!passwordIsValid) {
            errorText = "Password needs to be at least 6 characters"
            return
        }

        val user = User(
            email = email,
            name = name,
            adminflag = adminFlag,
            leaveallow = checkSixMonthsLeft(db_util.curDateTime(), getEndOfYearDate())
        )
        createUserScope.launch {
            postCreateUser(user)
        }
    }

    fun onCancelClicked() {
        mainViewModel.toggleCreateUserDialog()
    }
    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        if (mainViewModel.isLoading) {
            CircularLoadingBar()
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(MaterialTheme.spacing.spaceLarge),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(
                    id = R.color.white
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
        ) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.spaceLarge)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Text(
                    text = "Create User",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Name",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f),
                            value = name,
                            onValueChange = { newName -> name = newName },
                            isError = !nameIsValid,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Email",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(2f),
                            value = email,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            isError = !emailIsValid,
                            onValueChange = { newEmail -> email = newEmail },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = password,
                            onValueChange = { newPassword -> password = newPassword },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            isError = !passwordIsValid,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Admin",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Right
                        )
                        Column(
                            modifier = Modifier.weight(2f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box() {
                                    Checkbox(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .offset(x = -(16.dp), y = -(12.dp))
                                            .padding(0.dp),
                                        checked = adminFlag,
                                        onCheckedChange = { newAdminFlag ->
                                            adminFlag = newAdminFlag
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorResource(
                                                id = R.color.blue_500
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(
                            onClick = {
                                onSubmitClicked()
                            },
                            buttonText = "Submit"
                        )
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Cancel")
                    }
                }
            }
        }
    }
}

