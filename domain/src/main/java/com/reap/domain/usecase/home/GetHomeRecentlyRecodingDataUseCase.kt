package com.reap.domain.usecase.home

import com.reap.domain.model.RecordingMetaData
import com.reap.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHomeRecentlyRecodingDataUseCase @Inject constructor(
    private val homeRepository : HomeRepository
) {
    suspend operator fun invoke() : List<RecordingMetaData> {
        return homeRepository.getHomeRecentlyRecodingData()
    }
}