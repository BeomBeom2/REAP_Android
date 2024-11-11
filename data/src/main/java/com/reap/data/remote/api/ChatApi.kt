package com.reap.data.remote.api

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
    suspend fun postQuestion(
        @Body question: QuestionRequest
    ): Response<ResponseBody>

    @GET("/api/test/load")
    suspend fun getTestLoad(
    ): String
}
