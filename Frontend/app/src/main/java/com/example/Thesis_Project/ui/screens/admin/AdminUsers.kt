package com.example.Thesis_Project.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.HistoryCard
import com.example.Thesis_Project.ui.screens.history.correctionCardItems
import com.example.Thesis_Project.viewmodel.MainViewModel

@Composable
fun AdminUsersScreen (navController: NavController, mainViewModel: MainViewModel){
    AdminUsersContainer(navController = navController, mainViewModel = mainViewModel)
}
@Composable
fun AdminUsersContainer(navController: NavController, mainViewModel: MainViewModel){

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

    LazyColumn(
        modifier = Modifier.padding(MaterialTheme.spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Users",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = null,
                    tint = colorResource(id = R.color.blue_500)
                )
            }

        }
        items(correctionCardItems) { correctionCardItem ->
            HistoryCard(
                historyType = correctionCardItem.historyType,
                description = correctionCardItem.reason,
                date = correctionCardItem.dateGenerated,
                status = correctionCardItem.status
            )
        }
    }
}
