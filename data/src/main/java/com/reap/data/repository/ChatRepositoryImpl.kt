package com.reap.data.repository

import com.reap.data.remote.api.ChatApi
import com.reap.domain.model.QuestionRequest
import com.reap.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatapi: ChatApi
) : ChatRepository {
    override suspend fun postQuestion(question: String): String? {
        return try {
            val response = chatapi.postQuestion(QuestionRequest(question = question))
            if (response.isSuccessful) {
                response.body()?.answer // 서버 응답에서 answer 필드 추출
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
