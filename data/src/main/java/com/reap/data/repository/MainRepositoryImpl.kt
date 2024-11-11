package com.reap.data.repository

import com.reap.data.remote.api.MainApi
import com.reap.domain.repository.MainRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val mainApi : MainApi
) : MainRepository {
    override suspend fun postRecognizeUrl(topic: String, media: MultipartBody.Part): String {
        val response = mainApi.postRecognizeUrl(topic, media)
        return response.s3Url
    }
}
