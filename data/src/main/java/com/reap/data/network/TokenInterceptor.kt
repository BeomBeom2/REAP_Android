package com.reap.data.network

import android.content.Context
import android.util.Log
import com.reap.data.local.getAccessToken
import com.reap.data.local.saveAccessToken
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class TokenInterceptor(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val token = getAccessToken(context)  // 기존 토큰 가져오기
            Log.e("asd", "$token")
            val newToken = refreshToken(token)  // 토큰 갱신 로직
            return if (newToken != null) {
                saveAccessToken(context, newToken)
                response.request.newBuilder()
                    .header("Authorization", "$newToken")
                    .build()
            } else {
                null
            }
        }
        return null
    }

    private fun refreshToken(oldToken: String?): String? {
        try {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // 클라이언트 호출
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // 요청 생성
            val refreshTokenRequest = Request.Builder()
                .url("http://52.78.97.174:8080/token/refresh")
                .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), ""))
                .header("Authorization", "$oldToken")
                .build()

            val response = client.newCall(refreshTokenRequest).execute()

            // 응답 성공 시 처리
            if (response.isSuccessful) {
                val body = response.body.string()
                Log.e("TokenAuthenticator", "Response Body: $body")

                // JSON 파싱
                val jsonObject = JSONObject(body)
                return jsonObject.getString("accessToken")
            } else {
                Log.e("TokenAuthenticator", "Failed to refresh token: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "Failed to refresh token: ${e.localizedMessage}")
        }
        return null
    }

}
