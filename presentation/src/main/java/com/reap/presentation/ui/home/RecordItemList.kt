package com.reap.presentation.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reap.domain.model.RecordingMetaData

@Composable
fun RecordItemList(recordings: List<RecordingMetaData>,
                   onMenuClick: (scriptId: String, newName: String, newTopic: String) -> Unit,
                   onDeleteClick: (date: String, fileName: String, recordId: String) -> Unit){
    Text(
        modifier = Modifier
            .padding(bottom = 10.dp, top = 30.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        text = "최근 녹음"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        recordings.take(10).forEach { recording ->
            RecordingItem(
                recording = recording,
                onMenuClick = onMenuClick,
                onDeleteClick = onDeleteClick,
                onItemClick = { _, _ -> }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingItem(
    recording: RecordingMetaData,
    onMenuClick: (String, String, String) -> Unit,
    onDeleteClick: (String, String, String) -> Unit,
    onItemClick: (String, String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    var isNameChange by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    val topics = listOf("일상", "강의", "대화", "회의")
    var selectedTopic by remember { mutableStateOf(topics[0]) } // 첫 번째 값으로 초기화

    RecordingContent(recording, onItemClick) {
        showBottomSheet = true
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("이름 변경", modifier = Modifier.clickable {
                    showBottomSheet = false
                    isNameChange = true
                    showInputDialog = true
                })
                Spacer(modifier = Modifier.height(16.dp))
                Text("주제 변경", modifier = Modifier.clickable {
                    showBottomSheet = false
                    isNameChange = false
                    showInputDialog = true
                })
                Spacer(modifier = Modifier.height(16.dp))
                Text("삭제하기", modifier = Modifier.clickable {
                    showBottomSheet = false
                    onDeleteClick(recording.recordedDate, recording.fileName, recording.recordId)
                })
            }
        }
    }

    if (showInputDialog) {
        if (isNameChange) {
            // 이름 변경 다이얼로그
            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                title = { Text("이름 변경") },
                text = {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("새로운 이름을 입력하세요") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showInputDialog = false
                        onMenuClick(recording.recordId, inputText, recording.topic)
                        inputText = ""
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInputDialog = false }) {
                        Text("취소")
                    }
                }
            )
        } else {
            // 주제 변경 다이얼로그
            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                title = { Text("주제 변경") },
                text = {
                    Column {
                        topics.forEach { topic ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (topic == selectedTopic),
                                        onClick = { selectedTopic = topic }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (topic == selectedTopic),
                                    onClick = { selectedTopic = topic }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(topic)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showInputDialog = false
                        onMenuClick(recording.recordId, recording.fileName, selectedTopic)
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInputDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}


@Composable
fun RecordingContent(recording: RecordingMetaData, onItemClick: (String, String) -> Unit, onMenuClick: () -> Unit,) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick(recording.recordedDate, recording.fileName) },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "녹음 이름: ${recording.fileName.substringBeforeLast(".")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "주제: ${recording.topic}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "녹음 날짜: ${recording.recordedDate}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    }
}
