package com.reap.data.remote.api

import com.reap.domain.model.RecordingMetaData
import retrofit2.http.GET

interface HomeApi {
    @GET("audio/api/detail/record-script")
    suspend fun getHomeRecentlyRecodingData(
    ): List<RecordingMetaData>
}
