package com.reap.presentation.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.reap.data.saveAccessToken
import com.reap.presentation.BuildConfig
import com.reap.presentation.MainActivity
import com.reap.presentation.R
import com.reap.presentation.common.theme.REAPComposableTheme
import com.reap.presentation.ui.home.calendar.clickable
import com.reap.presentation.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            REAPComposableTheme(darkTheme = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        context = this,
                        onLogin = {
                        val intent = Intent(this, MainActivity::class.java)

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                        startActivity(intent)
                    })
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    context : Context,
    onLogin: () -> Unit
) {
    var showSplashScreen by remember { mutableStateOf(true) }

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
            Login(onLogin, context)
        }
    }
}

@Composable
internal fun Login(
    onLogin: () -> Unit,
    context : Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Image(
            bitmap = ImageBitmap.imageResource(id = R.mipmap.ic_logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.signature_1),
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.signature_1),
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center, // 가운데 정렬
            verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
        ) {
            Text(
                text = "회원가입",
                modifier = Modifier.clickable { }, // 회원가입 화면으로 이동
                color = colorResource(id = R.color.cement_5),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = " | ",
                color = colorResource(id = R.color.cement_5),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "비밀번호 찾기",
                modifier = Modifier.clickable {  }, // 비밀번호 찾기 화면으로 이동
                color = colorResource(id = R.color.cement_5),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.signature_1)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("로그인", color = Color.Black, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

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
            onClick = { createKakaoToken(onLogin, context)
                      },
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
                    modifier = Modifier.size(24.dp).padding(end = 8.dp),
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

fun createKakaoToken(onLogin: () -> Unit, context: Context) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        Handler(Looper.getMainLooper()).post {
            if (error != null) {
                Log.e("createKakaoToken", "카카오톡 로그인 실패, 에러 메시지 : $error")
                Toast.makeText(context, "카카오톡 로그인 실패, 에러 메시지 : $error", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Log.e("createKakaoToken", "AccessToken is $token")
                Toast.makeText(context, "카카오톡 로그인 성공, Token : $token", Toast.LENGTH_SHORT).show()
                saveAccessToken(context, token.accessToken)

                // 로그인 성공 시 콜백 호출
                onLogin()
            }
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context = context) { token, error ->
            Handler(Looper.getMainLooper()).post {
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.e("createKakaoToken", "카카오톡 로그인 실패, 에러 메시지 : $error")
                        return@post
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Toast.makeText(context, "카카오톡 로그인 성공, Token : $token", Toast.LENGTH_SHORT).show()
                    Log.e("createKakaoToken", "AccessToken is ${token.accessToken}")
                    saveAccessToken(context, token.accessToken)
                    // 로그인 성공 시 콜백 호출
                    onLogin()
                }
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = callback)
    }
}

fun saveAccessToken(context: Context, token: String) {
    Log.d("createKakaoToken", "Saving access token: $token")
    // 여기에 토큰 저장 로직 구현
}

@Preview
@Composable
private fun login() {
    Login(

        onLogin =   {},
        context = LocalContext.current
    )
}
