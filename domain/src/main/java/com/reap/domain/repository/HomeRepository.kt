package com.reap.domain.repository

import com.reap.domain.model.RecordingMetaData

interface HomeRepository {
    suspend fun getHomeRecentlyRecodingData() : List<RecordingMetaData>
}