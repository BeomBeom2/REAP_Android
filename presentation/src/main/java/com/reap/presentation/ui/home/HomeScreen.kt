package com.reap.presentation.ui.home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.data.getNickname
import com.reap.domain.model.RecordingDetail
import com.reap.presentation.model.RecordingDetailIntent
import com.reap.presentation.ui.dateRecList.DateRecDetailActivity
import com.reap.presentation.ui.home.calendar.CalendarCustom
import com.reap.presentation.ui.main.MainViewModel
import com.reap.presentation.ui.main.UploadStatus
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    LaunchedEffect(mainViewModel) {
        homeViewModel.getHomeRecentlyRecodingData()

        mainViewModel.uploadStatus.collect { res ->
            when (res) {
                is UploadStatus.Success -> {
                    Log.d("HomeScreen", "Upload success event received")
                    homeViewModel.getHomeRecentlyRecodingData()
                }

                else -> {}
            }
        }
    }

    Home(homeViewModel, navController)
}

@Composable
internal fun Home(
    viewModel: HomeViewModel, navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val sidePadding = screenWidth * 0.05f
    val scrollState = rememberScrollState()
    val recordings by viewModel.recentlyRecordings.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isFetchData by viewModel.isFetchData.collectAsState()

    LaunchedEffect(isFetchData) {
        if (isFetchData) {
            viewModel.resetToList()

            val intent = Intent(context, DateRecDetailActivity::class.java).apply {
                putParcelableArrayListExtra(
                    "DETAILS",
                    ArrayList(viewModel.selectedRecordingDetails.value?.map { it.toIntent() } ?: emptyList())
                )
                putExtra("SELECTED_DATE", viewModel.selectDate.value)
            }
            (context as Activity).startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle())
        }
    }



    Surface(color = com.reap.presentation.common.theme.BackgroundGray) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sidePadding)
                .verticalScroll(scrollState)
        ) {
            val nickname = getNickname(LocalContext.current) ?: "Reap"
            UserLabel(nickname, "")

            CalendarCustom(recordings, navController)

            RecordItemList(
                recordings = recordings,
                onMenuClick = { scriptId, newName, newTopic ->
                    coroutineScope.launch {
                        viewModel.updateTopicAndFileName(scriptId, newName, newTopic)
                    }
                },
                onDeleteClick = { scriptId, newName, newTopic ->
                    coroutineScope.launch {
                        viewModel.deleteRecord(scriptId, newName, newTopic)
                    }
                },
                onItemClick = { date, recordingId ->
                    viewModel.fetchRecordingDetails(date, recordingId)
                },
                navController = navController,
            )
        }
    }
}

fun RecordingDetail.toIntent(): RecordingDetailIntent {
    return RecordingDetailIntent(
        timestamp = this.timestamp,
        elapsedTime = this.elapsedTime,
        speaker = this.speaker,
        text = this.text
    )
}

