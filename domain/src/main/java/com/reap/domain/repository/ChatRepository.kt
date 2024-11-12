package com.reap.domain.repository

interface ChatRepository {
    suspend fun postQuestionStream(question : String) : String?

    suspend fun postQuestion(question : String) : String?
    suspend fun getTestLoad() : String
}