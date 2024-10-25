package com.reap.presentation.ui.selectedDateRecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun SelectedDateRecordScreen(
    navController: NavController,
    selectedDate: String
) {
//    val selectedDateRecordViewModel: SelectedDateRecordViewModel = hiltViewModel() // HomeViewModel 가져오기
//
//    LaunchedEffect(mainViewModel) {
//        Log.d("HomeScreen", "LaunchedEffect started")
//        selectedDateRecordViewModel.getHomeRecentlyRecodingData()
//
//        mainViewModel.onUploadSuccess.collect {
//            Log.d("HomeScreen", "Upload success event received")
//            homeViewModel.getHomeRecentlyRecodingData()
//        }
//    }

    SelectedDateRecord(navController, selectedDate)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectedDateRecord(navController: NavController, selectedDate: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                // 전달받은 날짜를 사용하여 표시
                val formattedDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)"))
                Text(
                    text = formattedDate,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // selectedDate를 사용하여 관련 녹음 데이터를 가져오는 로직 추가 가능
            // 예: viewModel.getRecordingsForDate(selectedDate)
//            recordingList.forEach { recording ->
//                RecordingItem(recording)
//            }
        }
    }
}
