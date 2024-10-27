package com.reap.presentation.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reap.domain.model.RecordingMetaData

@Composable
fun RecordItemList(recordings: List<RecordingMetaData>) {
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
        recordings.forEach { recording ->
            RecordingItem(recording, {})
        }
    }
}

@Composable
fun RecordingItem(recording: RecordingMetaData, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
    }
}