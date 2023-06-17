package com.example.Thesis_Project.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.R
import com.example.Thesis_Project.routes.AuthScreenRoutes

@Composable
fun LoginScreen(navController: NavController) {
    LoginContainer(navController)
}

@Composable
fun LoginContainer(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(MaterialTheme.spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.spaceExtraLarge,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Login("Login as Admin", R.drawable.admin, navController = navController)
        Login("Login as User", R.drawable.user, navController = navController)
    }
}

@Composable
fun Login(role: String, imageId: Int, navController: NavController) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = MaterialTheme.elevation.medium,
                shape = RoundedCornerShape(MaterialTheme.spacing.borderRadiusMedium)
            )
            .clip(RoundedCornerShape(MaterialTheme.spacing.borderRadiusMedium))
            .background(colorResource(id = R.color.gray_50))
            .padding(MaterialTheme.spacing.spaceLarge)
            .clickable {
                if (role.contains(
                        "User",
                        ignoreCase = true
                    )
                ) navController.navigate(AuthScreenRoutes.LoginUserScreen.route)
                else navController.navigate(AuthScreenRoutes.LoginAdminScreen.route)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .size(220.dp)
                .background(colorResource(id = R.color.gray_50))
        )
        Text(
            role,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    SecureMobileAttendanceSystemwithFaceRecognitionandEdgeComputingTheme {
//        LoginScreen(navController = rememberNavController())
//    }
//}