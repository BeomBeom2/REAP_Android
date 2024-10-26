package com.reap.data.repository

import com.reap.data.remote.api.HomeApi
import com.reap.domain.model.RecentlyRecording
import com.reap.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeApi : HomeApi
) : HomeRepository {
    override suspend fun getHomeRecentlyRecodingData(user: String): List<RecentlyRecording> {
        return homeApi.getHomeRecentlyRecodingData(user)
    }
}
