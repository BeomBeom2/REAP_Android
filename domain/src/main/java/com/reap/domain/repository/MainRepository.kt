package com.reap.domain.repository

import okhttp3.MultipartBody

interface MainRepository {
    suspend fun postRecognizeUrl(user: String, media: MultipartBody.Part) : String
}