package com.reap.presentation.ui.dateRecList

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import com.reap.presentation.ui.home.RecordingItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateRecListScreen(
    navController: NavController,
    selectedDate: String
) {
    val viewModel: DateRecListViewModel = hiltViewModel()
    val recordingList by viewModel.selectedDateRecordData.collectAsState()
    val selectedRecordingDetails by viewModel.selectedRecordingDetails.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isFetchData by viewModel.isFetchData.collectAsState()
    LaunchedEffect(isFetchData) {
        if (isFetchData) {
            viewModel.resetToList()
            viewModel.resetToList()

            val selectedDate = viewModel.selectDate.value ?: ""
            val detailsJson = Uri.encode(Gson().toJson(viewModel.selectedRecordingDetails.value ?: emptyList<List<RecordingDetail>>()))

            navController.navigate("dateRecScript/$selectedDate/$detailsJson")
        }
    }

    LaunchedEffect(selectedDate) {
        viewModel.getSelectedDateRecordData(selectedDate)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            viewModel.resetToList()
        }
    }

    DateRecList(
        selectedDate = selectedDate,
        recordingList = recordingList,
        onItemClick = { date, recordingId ->
            viewModel.fetchRecordingDetails(date, recordingId)
        },
        onBackPressed = { navController.popBackStack() },
        onMenuClick = { scriptId, newName, newTopic ->
            coroutineScope.launch {
                viewModel.updateTopicAndFileName(scriptId, newName, newTopic)
            }
        },
        onDeleteClick = { scriptId, newName, newTopic ->
            coroutineScope.launch {
                viewModel.deleteRecord(scriptId, newName, newTopic)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRecList(
    selectedDate: String,
    recordingList: List<RecordingMetaData>,
    onItemClick: (String, String) -> Unit,
    onBackPressed: () -> Unit,
    onMenuClick: (String, String, String) -> Unit,
    onDeleteClick: (String, String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                val formattedDate =
                    LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )


        Column(modifier = Modifier.padding(16.dp)) {
            recordingList.forEach { recording ->
                RecordingItem(recording, onMenuClick, onDeleteClick, onItemClick)
            }
        }
    }
}

@Composable
fun RecordingDetails(recordings: List<RecordingDetail>) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(recordings.size) { index ->
            val recording = recordings[index]

            val displayElapsedTime = if (recording.elapsedTime.startsWith("00:")) {
                recording.elapsedTime.substring(3)
            } else {
                recording.elapsedTime
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                // 화자와 시간을 한 행에 두 열로 배치
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "화자 " + recording.speaker,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = displayElapsedTime, // 처리된 시간을 표시
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // 메시지 텍스트를 다음 행에 배치
                Text(
                    text = recording.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
