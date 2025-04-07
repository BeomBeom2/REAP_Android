package com.reap.data.api

import com.reap.domain.model.RecordingMetaData
import com.reap.domain.model.UpdateTopicAndFileNameResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("/api/auth/meta/recent")
    suspend fun getHomeRecentlyRecodingData(
    ): List<RecordingMetaData>

    @PUT("/api/auth/script")
    suspend fun putUpdateTopicAndFileName(
        @Query("scriptId") scriptId: String,
        @Query("newName") newName: String,
        @Query("newTopic") newTopic: String
    ): UpdateTopicAndFileNameResponse

    @DELETE("/api/auth/script/{date}")
    suspend fun deleteRecord(
        @Path("date") date: String,
        @Query("fileName") fileName: String,
        @Query("recordId") recordId: String
    )
}
