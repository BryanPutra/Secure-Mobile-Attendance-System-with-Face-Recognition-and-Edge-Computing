package com.example.Thesis_Project.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.Thesis_Project.backend.db.db_models.CompanyParams
import com.example.Thesis_Project.backend.db.db_models.User
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

class AdminViewModel(mainViewModel: MainViewModel): ViewModel() {

    val auth = mainViewModel.auth
    val db = mainViewModel.db
    var currentUser: FirebaseUser? by mutableStateOf(mainViewModel.currentUser)

    var userData: User? by mutableStateOf(mainViewModel.userData)
    var companyVariable: CompanyParams? by mutableStateOf(null)

    val setCompanyVariable: (CompanyParams?) -> Unit = { newCompanyParams ->
        if (newCompanyParams != null) {
            companyVariable = newCompanyParams
        }
        Log.d("Get Admin Company variables", "Company Variables: $companyVariable")
    }

}