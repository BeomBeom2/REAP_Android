package com.reap.data.api

import com.reap.data.model.QuestionResponse
import com.reap.domain.model.QuestionRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatApi {
    @POST("/api/test/stream/ask")
    @Headers("Accept: text/event-stream")
    suspend fun postQuestionStream(
        @Body question: QuestionRequest
    ): Response<ResponseBody>

    @POST("/api/auth/ask")
    suspend fun postQuestion(
        @Body request: QuestionRequest
    ): Response<QuestionResponse>


    @GET("/api/test/load")
    suspend fun getTestLoad(
    ): String
}
