package com.reap.domain.repository

import com.reap.domain.model.RecordingMetaData
import com.reap.domain.model.UpdateTopicAndFileNameResponse

interface HomeRepository {
    suspend fun getHomeRecentlyRecodingData() : List<RecordingMetaData>
    suspend fun putUpdateTopicAndFileName(scriptId : String, newName : String, newTopic : String) : UpdateTopicAndFileNameResponse
    suspend fun deleteRecord(date : String, fileName : String, recordId : String)
}