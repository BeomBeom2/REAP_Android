package com.example.reap_service.recording

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/user")
    fun sendSpeechResult(@Query("name") name: String): Call<Void>

    @Multipart
    @POST("/recognize-url")
    fun uploadAudioFile(
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody,
        @Part("completion") completion: RequestBody,
        @Part("date") date: RequestBody
    ): Call<ResponseBody>

    @POST("send-question")
    fun sendQuestion(@Body textData: RequestBody): Call<ResponseBody>
}