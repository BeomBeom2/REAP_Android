package com.reap.data.repository

import com.reap.data.remote.api.HomeApi
import com.reap.domain.model.RecordingMetaData
import com.reap.domain.model.UpdateTopicAndFileNameResponse
import com.reap.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeApi : HomeApi
) : HomeRepository {
    override suspend fun getHomeRecentlyRecodingData(): List<RecordingMetaData> {
        return homeApi.getHomeRecentlyRecodingData()
    }

    override suspend fun putUpdateTopicAndFileName(
        scriptId: String,
        newName: String,
        newTopic: String
    ): UpdateTopicAndFileNameResponse {
        return homeApi.putUpdateTopicAndFileName(scriptId, newName, newTopic)
    }

    override suspend fun deleteRecord(date: String, fileName: String, recordId: String) {
        return homeApi.deleteRecord(date, fileName, recordId)
    }
}
