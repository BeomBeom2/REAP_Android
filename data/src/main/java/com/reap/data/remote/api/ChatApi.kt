package com.reap.data.remote.api

import com.reap.data.model.QuestionResponse
import com.reap.domain.model.QuestionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatApi {
    @POST("/api/auth/ask")
    suspend fun postQuestion(
        @Body request: QuestionRequest
    ): Response<QuestionResponse>

    @GET("/api/test/load")
    suspend fun getTestLoad(
    ): String
}
