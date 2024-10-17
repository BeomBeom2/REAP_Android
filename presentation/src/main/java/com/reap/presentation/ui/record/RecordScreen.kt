package com.reap.presentation.ui.record

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.reap.presentation.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordScreen(navController: NavController, context: Context) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val viewModel: RecordViewModel = hiltViewModel()

    when {
        permissionState.status.isGranted -> {
            Record(navController, viewModel)
        }
        permissionState.status.shouldShowRationale -> {
            SettingsRedirectDialog(navController, context)
        }
        else -> {
            LaunchedEffect(key1 = true) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
internal fun Record(
    navController: NavController,
    viewModel: RecordViewModel
) {
    val recordingState by viewModel.recordingState.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val recordingTime by viewModel.recordingTime.collectAsState()
    val volumeLevels by viewModel.volumeLevels.collectAsState()
    val currentTime = SimpleDateFormat("yyyy. MM. dd. a hh:mm 녹음", Locale.getDefault()).format(Date())

    var showDialog by remember { mutableStateOf(false) }
    var topic by remember { mutableStateOf("") }

    if (showDialog) {
        TopicDialog(
            topic = topic,
            onTopicChange = { topic = it },
            onConfirm = {
                val uri = Uri.fromFile(File(viewModel.recorder.currentFilePath))
                viewModel.stopRecordingAndUpload()
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (viewModel.recordingState.value == RecordViewModel.RecordingState.RECORDING) {
                viewModel.recorder.stopRecording()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = "취소", color = Color.White, fontSize = 16.sp)
            }
            TextButton(onClick = { showDialog = true }) {
                Text(text = "저장", color = Color.White, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (0.1f * LocalContext.current.resources.displayMetrics.heightPixels / LocalContext.current.resources.displayMetrics.density).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚨 유의사항",
                color = Color(0xCCFFFF00),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF333333), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "- 최대 2시간까지 녹음할 수 있습니다.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "- 녹음 중 다른 앱을 사용하면 오류가 발생할 수 있습니다.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = (0.3f * LocalContext.current.resources.displayMetrics.heightPixels / LocalContext.current.resources.displayMetrics.density).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime,
                color = colorResource(id = R.color.cement_5),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            VolumeBar(volumeLevels)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = String.format(
                    "%02d : %02d : %02d",
                    recordingTime / 3600,
                    (recordingTime % 3600) / 60,
                    recordingTime % 60
                ),
                color = colorResource(id = R.color.cement_5),
                fontSize = 28.sp,
                fontWeight = FontWeight.W400
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 15.dp)
        ) {
            IconButton(
                onClick = {
                    when (recordingState) {
                        RecordViewModel.RecordingState.RECORDING -> viewModel.pauseRecording()
                        RecordViewModel.RecordingState.PAUSED, RecordViewModel.RecordingState.IDLE -> viewModel.startRecording()
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = when (recordingState) {
                        RecordViewModel.RecordingState.RECORDING -> painterResource(id = R.drawable.ic_record_resume)
                        else -> painterResource(id = R.drawable.ic_record_start)
                    },
                    contentDescription = when (recordingState) {
                        RecordViewModel.RecordingState.RECORDING -> "Pause Recording"
                        else -> "Start Recording"
                    },
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )
            }

        }
    }
}


@Composable
fun TopicDialog(
    topic: String,
    onTopicChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        title = { Text("토픽 지정") },
        text = {
            Column {
                Text("녹음에 대한 토픽을 지정해주세요:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = topic,
                    onValueChange = onTopicChange,
                    label = { Text("토픽") },
                    singleLine = true
                )
            }
        }
    )
}

@Composable
fun VolumeBar(volumeLevels: List<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        volumeLevels.forEach { level ->
            Box(
                modifier = Modifier
                    .size(10.dp, 40.dp)
                    .padding(4.dp)
                    .background(if (level > 3) Color.White else Color.Gray)
            )
        }
    }
}

@Composable
fun SettingsRedirectDialog(navController: NavController, context: Context) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("권한 설정 필요") },
        text = {
            Text(
                "앱의 모든 기능을 사용하기 위해 녹음 권한이 필요합니다. " +
                        "확인을 눌러 설정에서 권한을 허용해 주세요."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            ) {
                Text("설정으로 이동")
            }
        },
        dismissButton = {
            Button(onClick = { navController.popBackStack() }) {
                Text("취소")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    RecordScreen(
        navController = NavController(LocalContext.current),
        context = LocalContext.current
    )
}