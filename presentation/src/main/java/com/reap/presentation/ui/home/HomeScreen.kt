package com.reap.presentation.ui.home

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.presentation.ui.home.calendar.CalendarCustom
import com.reap.presentation.ui.main.MainViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel
     ) {
    val homeViewModel: HomeViewModel = hiltViewModel() // HomeViewModel 가져오기

    LaunchedEffect(mainViewModel) {
        Log.d("HomeScreen", "LaunchedEffect started")
        homeViewModel.getHomeRecentlyRecodingData()

        mainViewModel.onUploadSuccess.collect {
            Log.d("HomeScreen", "Upload success event received")
            homeViewModel.getHomeRecentlyRecodingData()

            mainViewModel.resetUploadSuccess()
        }
    }

    Home(homeViewModel, navController)
}

@Composable
internal fun Home(
    viewModel: HomeViewModel, navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val sidePadding = screenWidth * 0.05f
    val scrollState = rememberScrollState()

    // HomeViewModel의 데이터를 관찰
    val recordings by viewModel.recentlyRecordingData.collectAsState()

    Surface(color = com.reap.presentation.common.theme.BackgroundGray) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sidePadding)
                .verticalScroll(scrollState)
        ) {
            UserLabel("Reap", "Reap@Kakao.com")

            CalendarCustom(recordings, navController)

            RecordItemList(recordings = recordings)
        }
    }
}

/*
@Preview
@Composable
private fun HomeScreen() {
    Home(
        viewModel = hiltViewModel(),
    )
}
 */
