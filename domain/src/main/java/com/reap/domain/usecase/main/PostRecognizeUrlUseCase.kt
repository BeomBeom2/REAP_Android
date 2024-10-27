package com.reap.domain.usecase.main

import com.reap.domain.repository.MainRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRecognizeUrlUseCase @Inject constructor(
    private val mainRepository : MainRepository
) {
    suspend operator fun invoke(topic : String, media: MultipartBody.Part) : String {
        return mainRepository.postRecognizeUrl(topic, media)
    }
}