package com.reap.domain.usecase.chat

import com.reap.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostQuestionUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(request: String): String? {
        return chatRepository.postQuestion(request)
    }
}