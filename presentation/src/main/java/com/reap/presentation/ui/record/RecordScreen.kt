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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.reap.presentation.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordScreen(navController: NavController, context: Context) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val viewModel: RecordViewModel = hiltViewModel()

    when {
        permissionState.status.isGranted -> {
            // 권한이 허용된 경우, 녹음 화면을 표시
            Record(navController, viewModel)
        }
        permissionState.status.shouldShowRationale -> {
            // 권한이 필요한 이유를 설명하고, 사용자에게 권한을 요청
            SettingsRedirectDialog(navController, context)
        }
        else -> {
            // 자동으로 권한 요청 시도가 된 후에 권한이 거부된 경우, 설정으로 유도
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
    val isRecording by viewModel.isRecording.collectAsState()  // StateFlow를 collectAsState로 수집
    val recordingTime by viewModel.recordingTime.collectAsState()
    val volumeLevels by viewModel.volumeLevels.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "🚨 유의사항",
            color = Color.Yellow,
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
                    text = "- 최대 4시간까지 녹음할 수 있습니다.",
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
        Spacer(modifier = Modifier.height(40.dp))
        VolumeBar(volumeLevels)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = String.format("%02d:%02d:%02d", recordingTime / 3600, (recordingTime % 3600) / 60, recordingTime % 60),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = "취소", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (isRecording) viewModel.stopRecordingAndUpload() else viewModel.startRecording()
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = if (isRecording) painterResource(id = R.drawable.ic_record_pause) else painterResource(id = R.drawable.ic_record_start),
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun VolumeBar(volumeLevels: List<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        volumeLevels.forEach { level ->
            Box(
                modifier = Modifier
                    .size(10.dp, 30.dp)
                    .padding(2.dp)
                    .background(if (level > 5) Color.White else Color.Gray)
            )
        }
    }
}

@Composable
fun SettingsRedirectDialog(navController: NavController, context: Context) {
    AlertDialog(
        onDismissRequest = { /* Handle dismissal if necessary */ },
        title = { Text("권한 설정 필요") },
        text = {
            Text("앱의 모든 기능을 사용하기 위해 녹음 권한이 필요합니다. " +
                    "확인을 눌러 설정에서 권한을 허용해 주세요.")
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
