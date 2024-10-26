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
                Log.e("AuthCodeHandler", "AccessToken is ${token.accessToken}")
                val resultIntent = Intent()
                resultIntent.putExtra("accessToken", token.accessToken)
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }
}
