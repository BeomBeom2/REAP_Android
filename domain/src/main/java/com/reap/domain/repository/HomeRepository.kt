package com.reap.domain.repository

import com.reap.domain.model.RecentlyRecording

interface HomeRepository {
    suspend fun getHomeRecentlyRecodingData(user: String) : List<RecentlyRecording>
}