package com.reap.data.api

import com.reap.domain.model.AccessTokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApi {
    @GET("/api/oauth/kakao")
    suspend fun getAccessToken(
        @Query("accessToken") accessToken : String
    ): AccessTokenResponse
}
