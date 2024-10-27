package com.reap.data.repository

import com.reap.data.remote.api.HomeApi
import com.reap.domain.model.RecordingMetaData
import com.reap.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeApi : HomeApi
) : HomeRepository {
    override suspend fun getHomeRecentlyRecodingData(): List<RecordingMetaData> {
        return homeApi.getHomeRecentlyRecodingData()
    }
}
