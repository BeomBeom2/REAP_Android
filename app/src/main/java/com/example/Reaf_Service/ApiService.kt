package com.example.Reaf_Service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/user")
    fun sendSpeechResult(@Query("name") name: String): Call<Void>
}