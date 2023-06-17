package com.example.Thesis_Project.ui.screens.admin
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.Thesis_Project.ui.screens.history.HistoryContainer
import com.example.Thesis_Project.viewmodel.AdminViewModel
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminUsersScreen (navController: NavController, adminViewModel: AdminViewModel){
    AdminUsersContainer(navController = navController, adminViewModel = adminViewModel)
}

@Composable
fun AdminUsersContainer(navController: NavController, adminViewModel: AdminViewModel){

}
