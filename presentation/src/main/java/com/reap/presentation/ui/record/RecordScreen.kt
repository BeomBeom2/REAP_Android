package com.reap.presentation.ui.record

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = if (isRecording) "Recording..." else "Tap mic to start recording",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        IconButton(
            onClick = {
                if (isRecording) viewModel.stopRecordingAndUpload() else viewModel.startRecording()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = if (isRecording) painterResource(id = R.drawable.ic_kakao) else painterResource(id = R.drawable.ic_mike),
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording"
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
