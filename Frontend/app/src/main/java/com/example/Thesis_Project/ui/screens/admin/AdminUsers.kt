package com.example.Thesis_Project.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.components.AdminApproveUserDialog
import com.example.Thesis_Project.ui.components.AdminCreateUserDialog
import com.example.Thesis_Project.ui.components.AdminUsersRow
import com.example.Thesis_Project.ui.components.AdminViewUserDialog
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.*
import java.util.*

@Composable
fun AdminUsersScreen(navController: NavController, mainViewModel: MainViewModel) {
    AdminUsersContainer(navController = navController, mainViewModel = mainViewModel)
}

@Composable
fun AdminUsersContainer(navController: NavController, mainViewModel: MainViewModel) {
    val isLaunched by rememberSaveable { mutableStateOf(mainViewModel.isAdminUsersInit) }
    Log.d("isLaunched", isLaunched.toString())

    var userQuerySearch by rememberSaveable { mutableStateOf("") }
    var searchIsActive by rememberSaveable { mutableStateOf(false) }
    val lastUserItemIndex by rememberSaveable { mutableStateOf((mainViewModel.usersList?.size)) }
    val searchedItems = remember { mutableStateListOf<String>() }
    val filteredUserQuery = remember { mutableStateListOf<User>() }

    var selectedViewUser by remember { mutableStateOf<User?>(null)}

    fun appendUsersList() {
        if (mainViewModel.usersList != null) {
            filteredUserQuery.clear()
            for (i in mainViewModel.usersList!!) {
                filteredUserQuery.add(i)
            }
        }
    }

    LaunchedEffect(key1 = isLaunched) {
        if (!isLaunched) {
            db_util.getAllUser(mainViewModel.db, mainViewModel.setUserList)
            appendUsersList()
            mainViewModel.setIsAdminUsersInit(true)
            Log.d("shit 1", filteredUserQuery.isEmpty().toString())
        }
    }

    LaunchedEffect(mainViewModel.usersList) {
        appendUsersList()
    }

    fun searchUsers(searchValue: String) {
        filteredUserQuery.clear()
        if (searchValue.isEmpty()) {
            appendUsersList()
            return
        }
        val tempFilteredUsers =
            mainViewModel.usersList?.filter {
                it.name?.lowercase()?.contains(searchValue.lowercase()) ?: false
            }
        if (tempFilteredUsers != null) {
            for (i in tempFilteredUsers) {
                filteredUserQuery.add(i)
            }
        }
        searchedItems.add(userQuerySearch)
        userQuerySearch = ""
    }

    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Users",
                style = MaterialTheme.typography.headlineSmall,
            )
            Icon(
                modifier = Modifier
                    .size(MaterialTheme.spacing.iconLarge)
                    .clickable {
                        mainViewModel.toggleCreateUserDialog()
                    },
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = colorResource(id = R.color.blue_500)
            )
        }
        SearchBar(
            query = userQuerySearch,
            onQueryChange = { userQuerySearch = it },
            onSearch = {
                searchUsers(userQuerySearch)
                searchIsActive = false
            },
            active = searchIsActive,
            onActiveChange = {
                searchIsActive = it
            },
            placeholder = {
                Text(text = "Search Users")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchIsActive) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (userQuerySearch.isNotEmpty()) {
                                userQuerySearch = ""
                            } else {
                                searchIsActive = false
                            }
                        },
                        imageVector = Icons.Filled.HighlightOff,
                        contentDescription = "close search"
                    )
                }
            }
        )
        {
            searchedItems.forEach {
                Row(
                    modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
                    horizontalArrangement = spacedBy(MaterialTheme.spacing.spaceMedium)
                ) {
                    Icon(imageVector = Icons.Filled.History, contentDescription = "search history")
                    Text(text = it)
                }
            }
        }
        if (mainViewModel.usersList != null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)) {
                itemsIndexed(filteredUserQuery) { index, userItem ->
                    AdminUsersRow(user = userItem, onViewClick = { viewItem ->
                        selectedViewUser = viewItem
                        mainViewModel.toggleViewUserDialog()
                    }) {
                        approveItem ->
                        selectedViewUser = approveItem
                        mainViewModel.toggleApproveUserRequestDialog()
                    }
                    if (index != lastUserItemIndex?.minus(1)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colorResource(id = R.color.gray_400))
                        )
                    }
                }
            }
        }
        if (mainViewModel.isCreateUserDialogShown) {
            AdminCreateUserDialog(mainViewModel = mainViewModel)
        }
        if (mainViewModel.isViewUserDialogShown) {
            AdminViewUserDialog(mainViewModel = mainViewModel, user = selectedViewUser) {
                selectedViewUser = null
                mainViewModel.toggleViewUserDialog()
            }
        }
        if (mainViewModel.isApproveUserRequestDialogShown) {
            AdminApproveUserDialog(mainViewModel = mainViewModel, user = selectedViewUser) {
                selectedViewUser = null
                mainViewModel.toggleApproveUserRequestDialog()
            }
        }
    }
}
