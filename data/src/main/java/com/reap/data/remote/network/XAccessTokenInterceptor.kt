package com.kust.kustaurant.data.di

import android.content.Context
import com.reap.data.getAccessToken
import okhttp3.Interceptor
import okhttp3.Response

class JwtTokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val jwtToken = getAccessToken(context)

        if (jwtToken != null) {
            requestBuilder.addHeader("Authorization", "$jwtToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
