package com.example.Thesis_Project.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.Thesis_Project.R
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import com.example.Thesis_Project.backend.db.db_models.User
import com.example.Thesis_Project.backend.db.db_util
import com.example.Thesis_Project.elevation
import com.example.Thesis_Project.spacing
import com.example.Thesis_Project.ui.utils.*
import com.example.Thesis_Project.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Composable
fun AdminApproveUserDialog(mainViewModel: MainViewModel, user: User?, onCancelClicked: () -> Unit) {

    val approveCorrectionScope = rememberCoroutineScope()
    val rejectCorrectionScope = rememberCoroutineScope()
    val approveLeaveScope = rememberCoroutineScope()
    val rejectLeaveScope = rememberCoroutineScope()

    var selectedCorrection by remember { mutableStateOf<CorrectionRequest?>(null) }
    var selectedLeave by remember { mutableStateOf<LeaveRequest?>(null) }

    val context: Context = LocalContext.current

    var correctionQuerySearch by rememberSaveable { mutableStateOf("") }
    var searchCorrectionIsActive by rememberSaveable { mutableStateOf(false) }
    val searchedCorrectionItems = remember { mutableStateListOf<String>() }
    val filteredCorrectionQuery = remember { mutableStateListOf<CorrectionRequest>() }

    var leaveQuerySearch by rememberSaveable { mutableStateOf("") }
    var searchLeaveIsActive by rememberSaveable { mutableStateOf(false) }
    val searchedLeaveItems = remember { mutableStateListOf<String>() }
    val filteredLeaveQuery = remember { mutableStateListOf<LeaveRequest>() }

    var correctionSelected by rememberSaveable { mutableStateOf(true) }
    var leaveSelected by rememberSaveable { mutableStateOf(false) }

    val onCorrectionTabClicked: () -> Unit = {
        correctionSelected = true
        leaveSelected = false
    }

    val onLeaveTabClicked: () -> Unit = {
        leaveSelected = true
        correctionSelected = false
    }

    var correctionRequests: List<CorrectionRequest>? by rememberSaveable { mutableStateOf(null) }
    var leaveRequests: List<LeaveRequest>? by rememberSaveable { mutableStateOf(null) }
    var lastCorrectionItemIndex by rememberSaveable { mutableStateOf((correctionRequests?.size)) }
    var lastLeaveItemIndex by rememberSaveable { mutableStateOf((leaveRequests?.size)) }

    var viewCorrectionDialogShown by rememberSaveable { mutableStateOf(false) }
    var viewLeaveDialogShown by rememberSaveable { mutableStateOf(false) }
    var approveCorrectionDialogShown by rememberSaveable { mutableStateOf(false) }
    var rejectCorrectionDialogShown by rememberSaveable { mutableStateOf(false) }
    var approveLeaveDialogShown by rememberSaveable { mutableStateOf(false) }
    var rejectLeaveDialogShown by rememberSaveable { mutableStateOf(false) }

    fun appendCorrectionList() {
        if (correctionRequests != null) {
            filteredCorrectionQuery.clear()
            for (i in correctionRequests!!) {
                filteredCorrectionQuery.add(i)
            }
        }
    }

    fun appendLeaveList() {
        if (leaveRequests != null) {
            filteredLeaveQuery.clear()
            for (i in leaveRequests!!) {
                filteredLeaveQuery.add(i)
            }
        }
    }

    suspend fun getUserData() {
        coroutineScope {
            launch {
                db_util.getCorrectionRequest(
                    mainViewModel.db,
                    user?.userid,
                ) { newCorrectionRequests ->
                    correctionRequests = newCorrectionRequests?.filter {
                        (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                    }
                    Log.e("fetching new correction data", "$newCorrectionRequests")
                    Log.e("fetching new correction datas", "$correctionRequests")
                    if (newCorrectionRequests != null) {
                        lastCorrectionItemIndex = correctionRequests?.size
                    }
                }
            }
            launch {
                db_util.getLeaveRequest(
                    mainViewModel.db,
                    user?.userid,
                ) { newLeaveRequests ->
                    leaveRequests = newLeaveRequests?.filter {
                        (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                    }
                    Log.e("fetching correction data1", "$correctionRequests")
                    if (newLeaveRequests != null) {
                        lastLeaveItemIndex = leaveRequests?.size
                    }
                }
            }
        }
        Log.e("fetching correction data2", "fetching correction data")
    }

    LaunchedEffect(Unit) {
        runBlocking {
            Log.e("fetching correction data", "fetching correction data")
            mainViewModel.setIsLoading(true)
            getUserData()
            appendCorrectionList()
            appendLeaveList()
            mainViewModel.setIsLoading(false)
            Log.e("fetching correction data1", "fetching correction data")
        }
    }

    LaunchedEffect(correctionRequests) {
        appendCorrectionList()
    }

    LaunchedEffect(leaveRequests) {
        appendLeaveList()
    }

    fun searchCorrectionRequest(searchValue: String) {
        filteredCorrectionQuery.clear()
        if (searchValue.isEmpty()) {
            appendCorrectionList()
            return
        }

        val tempFilteredCorrection =
            correctionRequests?.filter {
                formatDateToStringWithOrdinal(it.createdate)?.lowercase()?.contains(searchValue.lowercase()) ?: false
            }
        if (tempFilteredCorrection != null) {
            for (i in tempFilteredCorrection) {
                filteredCorrectionQuery.add(i)
            }
        }
        searchedCorrectionItems.add(correctionQuerySearch)
        correctionQuerySearch = ""
    }

    fun searchLeaveRequest(searchValue: String) {
        filteredLeaveQuery.clear()
        if (searchValue.isEmpty()) {
            appendLeaveList()
            return
        }

        val tempFilteredLeave =
            leaveRequests?.filter {
                formatDateToStringWithOrdinal(it.createdate)?.lowercase()
                    ?.contains(searchValue.lowercase()) ?: false
            }
        if (tempFilteredLeave != null) {
            for (i in tempFilteredLeave) {
                filteredLeaveQuery.add(i)
            }
        }
        searchedCorrectionItems.add(correctionQuerySearch)
        correctionQuerySearch = ""
    }

    val postApproveCorrectionRequest: suspend (correctionRequest: CorrectionRequest) -> Unit =
        { correctionRequestItem ->
            approveCorrectionDialogShown = false
            mainViewModel.setIsLoading(true)
            try {
                db_util.approveCorrectionRequest(
                    mainViewModel.db,
                    correctionRequestItem,
                    mainViewModel.userData!!,
                    mainViewModel.companyVariable!!
                ) { approveCorrectionSuccess ->
                    if (approveCorrectionSuccess) {
                        db_util.getCorrectionRequest(
                            mainViewModel.db,
                            user?.userid,
                        ) { newCorrectionRequests ->
                            correctionRequests = newCorrectionRequests?.filter {
                                (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                            }
                            if (newCorrectionRequests != null) {
                                lastCorrectionItemIndex = correctionRequests?.size
                            }
                        }
                        mainViewModel.showToast(
                            context,
                            "Correction Request approved successfully"
                        )
                    } else {
                        mainViewModel.showToast(context, "Failed to approve correction request")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(context, "Failed to approve correction request: $e")
                Log.e("Error", "Failed to approve leave request: $e")
            }
            selectedCorrection = null
            mainViewModel.setIsLoading(false)
        }

    val postRejectCorrectionRequest: suspend (correctionRequest: CorrectionRequest) -> Unit =
        { correctionRequestItem ->
            rejectCorrectionDialogShown = false
            mainViewModel.setIsLoading(true)
            try {
                db_util.rejectCorrectionRequest(
                    mainViewModel.db,
                    correctionRequestItem.correctionrequestid!!,
                    mainViewModel.userData?.userid!!,
                ) { approveCorrectionSuccess ->
                    if (approveCorrectionSuccess) {
                        db_util.getCorrectionRequest(
                            mainViewModel.db,
                            user?.userid,
                        ) { newCorrectionRequests ->
                            correctionRequests = newCorrectionRequests?.filter {
                                (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                            }
                            if (newCorrectionRequests != null) {
                                lastCorrectionItemIndex = correctionRequests?.size
                            }
                        }
                        mainViewModel.showToast(
                            context,
                            "Correction Request rejected successfully"
                        )
                    } else {
                        mainViewModel.showToast(context, "Failed to reject correction request")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(context, "Failed to reject correction request: $e")
                Log.e("Error", "Failed to reject leave request: $e")
            }
            selectedCorrection = null
            mainViewModel.setIsLoading(false)
        }

    val postApproveLeaveRequest: suspend (leaveRequest: LeaveRequest) -> Unit =
        { leaveRequestItem ->
            approveLeaveDialogShown = false
            mainViewModel.setIsLoading(true)
            try {
                db_util.approveLeaveRequest(
                    mainViewModel.db,
                    leaveRequestItem,
                    mainViewModel.userData!!,
                    mainViewModel.companyVariable!!
                ) { approveLeaveSuccess ->
                    if (approveLeaveSuccess) {
                        db_util.getLeaveRequest(
                            mainViewModel.db,
                            user?.userid,
                        ) { newLeaveRequests ->
                            leaveRequests = newLeaveRequests?.filter {
                                (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                            }
                            if (newLeaveRequests != null) {
                                lastLeaveItemIndex = leaveRequests?.size
                            }
                        }
                        mainViewModel.showToast(
                            context,
                            "Leave Request approved successfully"
                        )
                    } else {
                        mainViewModel.showToast(context, "Failed to approve leave request")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(context, "Failed to approve leave request: $e")
                Log.e("Error", "Failed to approve leave request: $e")
            }
            selectedLeave = null
            mainViewModel.setIsLoading(false)
        }

    val postRejectLeaveRequest: suspend (leaveRequest: LeaveRequest) -> Unit =
        { leaveRequestItem ->
            rejectLeaveDialogShown = false
            mainViewModel.setIsLoading(true)
            try {
                db_util.rejectLeaveRequest(
                    mainViewModel.db,
                    leaveRequestItem.leaverequestid!!,
                    mainViewModel.userData?.userid!!,
                ) { approveLeaveSuccess ->
                    if (approveLeaveSuccess) {
                        db_util.getLeaveRequest(
                            mainViewModel.db,
                            user?.userid,
                        ) { newLeaveRequests ->
                            leaveRequests = newLeaveRequests?.filter {
                                (it.approvedflag == false || it.approvedflag == null) && (it.rejectedflag == false || it.rejectedflag == null)
                            }
                            if (newLeaveRequests != null) {
                                lastLeaveItemIndex = leaveRequests?.size
                            }
                        }
                        mainViewModel.showToast(
                            context,
                            "Leave Request rejected successfully"
                        )
                    } else {
                        mainViewModel.showToast(context, "Failed to reject leave request")
                    }
                }
            } catch (e: Exception) {
                mainViewModel.showToast(context, "Failed to reject leave request: $e")
                Log.e("Error", "Failed to reject leave request: $e")
            }
            selectedLeave = null
            mainViewModel.setIsLoading(false)
        }


    fun onApproveCorrectionClicked() {
        approveCorrectionScope.launch {
            if (selectedCorrection != null) {
                postApproveCorrectionRequest(selectedCorrection!!)
            }
        }
    }

    fun onRejectCorrectionClicked() {
        rejectCorrectionScope.launch {
            if (selectedCorrection != null) {
                postRejectCorrectionRequest(selectedCorrection!!)
            }
        }
    }

    fun onApproveLeaveClicked() {
        approveLeaveScope.launch {
            if (selectedLeave != null) {
                postApproveLeaveRequest(selectedLeave!!)
            }
        }
    }

    fun onRejectLeaveClicked() {
        rejectLeaveScope.launch {
            if (selectedLeave != null) {
                postRejectLeaveRequest(selectedLeave!!)
            }
        }
    }

    if (mainViewModel.isLoading) {
        CircularLoadingBar()
    }

    if (viewCorrectionDialogShown) {
        AdminViewUserCorrectionDialog(
            correctionRequest = selectedCorrection,
            mainViewModel = mainViewModel
        ) {
            selectedCorrection = null
            viewCorrectionDialogShown = false
        }
    }

    if (approveCorrectionDialogShown) {
        AlertDialog(
            onDismissRequest = {
                approveCorrectionDialogShown = false
                selectedCorrection = null
            },
            title = { Text(text = "Approve Correction Request") },
            text = { Text(text = "Are you sure you want to approve the correction request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onApproveCorrectionClicked()
                    }
                ) {
                    Text(text = "Approve")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        approveCorrectionDialogShown = false
                        selectedCorrection = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (rejectCorrectionDialogShown) {
        AlertDialog(
            onDismissRequest = {
                rejectCorrectionDialogShown = false
                selectedCorrection = null
            },
            title = { Text(text = "Reject Correction Request") },
            text = { Text(text = "Are you sure you want to reject the correction request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectCorrectionClicked()
                    }
                ) {
                    Text(text = "Reject")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        rejectCorrectionDialogShown = false
                        selectedCorrection = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (viewLeaveDialogShown) {
        AdminViewUserLeaveDialog(
            leaveRequest = selectedLeave,
            mainViewModel = mainViewModel
        ) {
            selectedLeave = null
            viewLeaveDialogShown = false
        }
    }

    if (approveLeaveDialogShown) {
        AlertDialog(
            onDismissRequest = {
                approveLeaveDialogShown = false
                selectedLeave = null
            },
            title = { Text(text = "Approve Leave Request") },
            text = { Text(text = "Are you sure you want to approve the leave request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onApproveLeaveClicked()
                    }
                ) {
                    Text(text = "Approve")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        approveLeaveDialogShown = false
                        selectedLeave = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (rejectLeaveDialogShown) {
        AlertDialog(
            onDismissRequest = {
                rejectLeaveDialogShown = false
                selectedLeave = null
            },
            title = { Text(text = "Reject Leave Request") },
            text = { Text(text = "Are you sure you want to reject the leave request?") },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectLeaveClicked()
                    }
                ) {
                    Text(text = "Reject")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        rejectLeaveDialogShown = false
                        selectedLeave = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = { onCancelClicked() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceLarge)
            ) {
                Text(
                    text = user?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceLarge)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HistoryNavButton(
                        isSelected = correctionSelected,
                        historyType = "Correction",
                        onClicked = onCorrectionTabClicked,
                        isAdminApprovePage = true
                    )
                    HistoryNavButton(
                        isSelected = leaveSelected,
                        historyType = "Leave",
                        onClicked = onLeaveTabClicked,
                        isAdminApprovePage = true
                    )
                }
                if (correctionSelected) {
                    SearchBar(
                        query = correctionQuerySearch,
                        onQueryChange = { correctionQuerySearch = it },
                        onSearch = {
                            searchCorrectionRequest(correctionQuerySearch)
                            searchCorrectionIsActive = false
                        },
                        active = searchCorrectionIsActive,
                        onActiveChange = {
                            searchCorrectionIsActive = it
                        },
                        placeholder = {
                            Text(text = "Search correction by created date", fontSize = 11.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchCorrectionIsActive) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        if (correctionQuerySearch.isNotEmpty()) {
                                            correctionQuerySearch = ""
                                        } else {
                                            searchCorrectionIsActive = false
                                        }
                                    },
                                    imageVector = Icons.Filled.HighlightOff,
                                    contentDescription = "close search"
                                )
                            }
                        }
                    )
                    {
                        searchedCorrectionItems.forEach {
                            Row(
                                modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
                                horizontalArrangement = Arrangement.Absolute.spacedBy(MaterialTheme.spacing.spaceMedium)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = "search history"
                                )
                                Text(text = it)
                            }
                        }
                    }
                    if (correctionRequests != null && correctionRequests!!.isNotEmpty()) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)) {
                            itemsIndexed(filteredCorrectionQuery) { index, correctionRequestItem ->
                                AdminUsersApproveCorrectionRow(
                                    mainViewModel = mainViewModel,
                                    correctionRequest = correctionRequestItem,
                                    onViewClick = { viewItem ->
                                        selectedCorrection = viewItem
                                        viewCorrectionDialogShown = true
                                    },
                                    onApproveClick = { approveItem ->
                                        selectedCorrection = approveItem
                                        approveCorrectionDialogShown = true
                                    }, onRejectClick = { rejectItem ->
                                        selectedCorrection = rejectItem
                                        rejectCorrectionDialogShown = true
                                    })
                                if (index != lastCorrectionItemIndex?.minus(1)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(colorResource(id = R.color.gray_400))
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No correction request found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = MaterialTheme.spacing.spaceMedium)
                        )
                    }
                } else {
                    SearchBar(
                        query = leaveQuerySearch,
                        onQueryChange = { leaveQuerySearch = it },
                        onSearch = {
                            searchLeaveRequest(leaveQuerySearch)
                            searchLeaveIsActive = false
                        },
                        active = searchLeaveIsActive,
                        onActiveChange = {
                            searchLeaveIsActive = it
                        },
                        placeholder = {
                            Text(text = "Search leave by created date", fontSize = 11.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchLeaveIsActive) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        if (leaveQuerySearch.isNotEmpty()) {
                                            leaveQuerySearch = ""
                                        } else {
                                            searchLeaveIsActive = false
                                        }
                                    },
                                    imageVector = Icons.Filled.HighlightOff,
                                    contentDescription = "close search"
                                )
                            }
                        }
                    )
                    {
                        searchedLeaveItems.forEach {
                            Row(
                                modifier = Modifier.padding(MaterialTheme.spacing.spaceLarge),
                                horizontalArrangement = Arrangement.Absolute.spacedBy(MaterialTheme.spacing.spaceMedium)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = "search history"
                                )
                                Text(text = it)
                            }
                        }
                    }
                    if (leaveRequests != null && leaveRequests!!.isNotEmpty()) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spaceMedium)) {
                            itemsIndexed(filteredLeaveQuery!!) { index, leaveRequestItem ->
                                AdminUsersApproveLeaveRow(
                                    mainViewModel = mainViewModel,
                                    leaveRequest = leaveRequestItem,
                                    onViewClick = { viewItem ->
                                        selectedLeave = viewItem
                                        viewLeaveDialogShown = true
                                    },
                                    onApproveClick = { approveItem ->
                                        selectedLeave = approveItem
                                        approveLeaveDialogShown = true
                                    }, onRejectClick = { rejectItem ->
                                        selectedLeave = rejectItem
                                        rejectLeaveDialogShown = true
                                    })
                                if (index != lastCorrectionItemIndex?.minus(1)) {
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
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.5f)) {
                        ButtonHalfWidth(onClick = { onCancelClicked() }, buttonText = "Close")
                    }
                }
            }
        }
    }
}

