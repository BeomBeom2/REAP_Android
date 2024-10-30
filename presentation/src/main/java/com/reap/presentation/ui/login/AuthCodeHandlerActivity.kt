package com.reap.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthCodeHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleKakaoLogin()
    }

    private fun handleKakaoLogin() {
        val context = this

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("AuthCodeHandler", "카카오톡 로그인 실패, 에러 메시지 : $error")
                setResult(RESULT_CANCELED)
            } else if (token != null) {
                Log.d("AuthCodeHandler", "AccessToken is ${token.accessToken}")

                // 사용자 정보 요청
                UserApiClient.instance.me { user, userError ->
                    if (userError != null) {
                        Log.e("AuthCodeHandler", "사용자 정보 요청 실패, 에러 메시지 : $userError")
                        setResult(RESULT_CANCELED)
                    } else if (user != null) {
                        val nickname = user.kakaoAccount?.profile?.nickname ?: "Unknown"

                        Log.d("AuthCodeHandler", "사용자 닉네임: $nickname")

                        // 닉네임과 액세스 토큰을 인텐트에 추가
                        val resultIntent = Intent().apply {
                            putExtra("accessToken", token.accessToken)
                            putExtra("nickname", nickname)
                        }
                        setResult(RESULT_OK, resultIntent)
                    }
                    finish()
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("AuthCodeHandler", "사용자 정보 요청 실패", error)
                setResult(RESULT_CANCELED)
            } else if (user != null) {
                Log.i("AuthCodeHandler", "사용자 정보 요청 성공")
                val userId = user.id
                val userName = user.kakaoAccount?.profile?.nickname

                val resultIntent = Intent().apply {
                    putExtra("userId", userId)
                    putExtra("userName", userName)
                }
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }
    }
}
