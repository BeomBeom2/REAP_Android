package com.reap.presentation.ui.selectedDateRecord

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.domain.model.RecordingDetailData
import com.reap.domain.model.RecordingMetaData
import com.reap.presentation.ui.home.RecordingItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SelectedDateRecordScreen(
    navController: NavController,
    selectedDate: String
) {
    val selectedDateRecordViewModel: SelectedDateRecordViewModel = hiltViewModel()
    val recordingList by selectedDateRecordViewModel.selectedDateRecordData.collectAsState()
    val screenState by selectedDateRecordViewModel.screenState.collectAsState()
    val selectedRecordingDetails by selectedDateRecordViewModel.selectedRecordingDetails.collectAsState()
    val errorMessage by selectedDateRecordViewModel.errorMessage.collectAsState()

    LaunchedEffect(selectedDate) {
        selectedDateRecordViewModel.getSelectedDateRecordData(selectedDate)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            //Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
            selectedDateRecordViewModel.resetToList()
        }
    }

    SelectedDateRecord(
        navController = navController,
        selectedDate = selectedDate,
        recordingList = recordingList,
        screenState = screenState,
        selectedRecordingDetails = selectedRecordingDetails,
        onRecordingClick = { selectedDate, recordingId ->
            selectedDateRecordViewModel.fetchRecordingDetails(selectedDate, recordingId)
        },
        onBackPressed = {
            when (screenState) {
                ScreenState.RECORD_LIST -> navController.popBackStack()
                else -> selectedDateRecordViewModel.resetToList()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectedDateRecord(
    navController: NavController,
    selectedDate: String,
    recordingList: List<RecordingMetaData>,
    screenState: ScreenState,
    selectedRecordingDetails: RecordingDetailData?,
    onRecordingClick: (String, String) -> Unit,
    onBackPressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                val formattedDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = formattedDate,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
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

        when (screenState) {
            ScreenState.RECORD_LIST -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    recordingList.forEach { recording ->
                        RecordingItem(recording) { onRecordingClick(selectedDate, recording.fileName) }
                    }
                }
            }
            ScreenState.RECORD_SCRIPT -> {
                selectedRecordingDetails?.let { details ->
                    RecordingDetails(details)
                }
            }
            ScreenState.RECORD_ERROR -> {
                // 에러 상태일 때는 아무것도 표시하지 않음 (Toast로 처리)
            }
        }
    }
}

@Composable
fun RecordingDetails(recordDetail: RecordingDetailData) {
    Text(
        text = recordDetail.text,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
}

/*

 */