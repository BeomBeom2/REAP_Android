package com.reap.presentation.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reap.domain.model.RecentRecording
import com.reap.domain.model.RecentlyRecording

// 샘플 데이터
val recordings = listOf(
    RecentRecording("녹음 1111111111111111111111111111111111111111111111", "대화", "2024-09-23"),
    RecentRecording("녹음 2", "회의", "2024-09-24"),
    RecentRecording("녹음 3", "강의", "2024-09-25"),
    RecentRecording("녹음 4", "전화 통화", "2024-09-26")
)

@Composable
fun RecordingsList(recordings: List<RecentlyRecording>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        recordings.forEach { recording ->
            RecordingItem(recording)
        }
    }
}

@Composable
fun RecordingItem(recording: RecentlyRecording) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "녹음 이름: ${recording.fileName}",
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
    }
}