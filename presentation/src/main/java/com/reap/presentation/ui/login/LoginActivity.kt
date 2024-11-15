package com.reap.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reap.data.saveAccessToken
import com.reap.data.saveNickname
import com.reap.presentation.MainActivity
import com.reap.presentation.R
import com.reap.presentation.common.theme.REAPComposableTheme
import com.reap.presentation.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var kakaoLoginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setKakaoLoginActivityForResult()

        setContent {
            REAPComposableTheme(darkTheme = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        context = this,
                        onLogin = {
                            Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        },
                        onStartKakaoLogin = { startKakaoLogin() },
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }

    private fun startKakaoLogin() {
        val intent = Intent(this, AuthCodeHandlerActivity::class.java)
        kakaoLoginLauncher.launch(intent)
    }

    private fun setKakaoLoginActivityForResult() {
        kakaoLoginLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val accessToken = data?.getStringExtra("accessToken")
                val nickname = data?.getStringExtra("nickname") // 닉네임 가져오기

                if (accessToken != null) {
                    loginViewModel.getAccessToken(accessToken)

                    // 닉네임이 존재하면 저장
                    if (nickname != null) {
                        saveNickname(this, nickname)
                    }
                } else {
                    Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            } else { // 로그인 실패
                Log.d("KakaoLogin", "카카오 로그인 실패")
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoginScreen(
    context: Context,
    onLogin: () -> Unit,
    onStartKakaoLogin: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val accessTokenState = loginViewModel.accessToken.collectAsState()
    var showSplashScreen by remember { mutableStateOf(true) }

    accessTokenState.value?.let { result ->
        if (result.success) {
            Log.d("LoginActivity", "AccessToken is ${result.jwtToken}")
            saveAccessToken(context, result.jwtToken)
            onLogin()
        } else {
            Log.d("LoginActivity", "JWT토큰 반환 실패")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showSplashScreen,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000)),
            modifier = Modifier.fillMaxSize()
        ) {
            SplashScreen(onSplashComplete = { showSplashScreen = false })
        }

        AnimatedVisibility(
            visible = !showSplashScreen,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            modifier = Modifier.fillMaxSize()
        ) {
            Login(onStartKakaoLogin)
        }
    }
}


@Composable
internal fun Login(
    onStartKakaoLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(id = R.mipmap.ic_logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "간편 로그인",
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(id = R.color.cement_5),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onStartKakaoLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.kakao)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_kakao),
                    contentDescription = "Kakao logo",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(
                    "카카오 로그인",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


