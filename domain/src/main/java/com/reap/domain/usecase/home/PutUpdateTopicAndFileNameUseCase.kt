package com.reap.domain.usecase.home

import com.reap.domain.model.UpdateTopicAndFileNameResponse
import com.reap.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PutUpdateTopicAndFileNameUseCase @Inject constructor(
    private val homeRepository : HomeRepository
) {
    suspend operator fun invoke(scriptId : String, newName : String, newTopic : String) : UpdateTopicAndFileNameResponse {
        return homeRepository.putUpdateTopicAndFileName(scriptId, newName, newTopic)
    }
}