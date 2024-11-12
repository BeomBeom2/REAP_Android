package com.reap.data.repository

import com.reap.data.remote.api.ChatApi
import com.reap.domain.model.QuestionRequest
import com.reap.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {
    override suspend fun postQuestionStream(question: String): String? {
        return try {
            val response = chatApi.postQuestionStream(QuestionRequest(question = question))
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    parseStreamResponse(responseBody)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun postQuestion(question: String): String? {
        return try {
            val response = chatApi.postQuestion(QuestionRequest(question = question))
            if (response.isSuccessful) {
                response.body()?.answer
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getTestLoad(): String {
        return chatApi.getTestLoad()
    }

    private suspend fun parseStreamResponse(responseBody: ResponseBody): String {
        val reader = responseBody.byteStream().bufferedReader()
        val stringBuilder = StringBuilder()

        try {
            while (true) {
                val line = withContext(Dispatchers.IO) {
                    reader.readLine()
                } ?: break
                if (line.isNotEmpty()) {
                    stringBuilder.append(line).append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.IO) {
                reader.close()
            }
        }

        return stringBuilder.toString()

    }
}
