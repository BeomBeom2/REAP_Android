package com.reap.presentation.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        }
    }

    Home(
        homeViewModel,
        )
}

@Composable
internal fun Home(
    viewModel: HomeViewModel,  // HomeViewModel을 받아옴
) {
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

            CalendarCustom(recordings)

            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 30.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = "최근 녹음"
            )

            // ViewModel의 데이터를 RecordingsList로 전달
            RecordingsList(recordings = recordings)
        }
    }
}



@Preview
@Composable
private fun HomeScreen() {
    Home(
        viewModel = hiltViewModel(),
    )
}
