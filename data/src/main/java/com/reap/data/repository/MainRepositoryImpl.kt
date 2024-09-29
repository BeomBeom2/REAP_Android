package com.reap.data.repository

import com.reap.data.remote.MainApi
import com.reap.domain.repository.MainRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val mainApi : MainApi
) : MainRepository {
    override suspend fun postRecognizeUrl(user: String, language: String, media: MultipartBody.Part): String {
        val userRequestBody = user.toRequestBody("text/plain".toMediaTypeOrNull())
        val languageRequestBody = language.toRequestBody("text/plain".toMediaTypeOrNull())
        return mainApi.postRecognizeUrl(userRequestBody, languageRequestBody, media)
    }
}
