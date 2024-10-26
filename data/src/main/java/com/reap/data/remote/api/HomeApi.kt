package com.reap.data.remote.api

import com.reap.domain.model.RecentlyRecording
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {
    @GET("audio/api/detail/{userId}/record-script")
    suspend fun getHomeRecentlyRecodingData(
        @Path("userId") user: String,
    ): List<RecentlyRecording>
}
