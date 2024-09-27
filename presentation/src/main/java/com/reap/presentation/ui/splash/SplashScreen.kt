package com.reap.presentation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reap.presentation.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(), // 화면 전체를 채우는 Column
        verticalArrangement = Arrangement.Center, // 세로 중앙 정렬
        horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
    ) {
        // 로고 또는 스플래시 이미지 표시
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Record Everything And Play",
            fontSize = 24.sp,
            color = colorResource(id = R.color.signature_1),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LaunchedEffect(key1 = true) {
            delay(2000)
            onSplashComplete()
        }
    }
}