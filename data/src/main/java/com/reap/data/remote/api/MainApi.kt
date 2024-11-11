package com.reap.data.remote.api

import com.reap.domain.model.RecognizeResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MainApi {
    @Multipart
    @POST("/api/auth/upload")
    suspend fun postRecognizeUrl(
        @Query("topic") topic: String,
        @Part media: MultipartBody.Part
    ): RecognizeResponse
}
