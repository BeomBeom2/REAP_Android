package com.reap.data.remote.api

import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SelectedDateRecordApi {
    @GET("/api/auth/meta/{date}")
    suspend fun getSelectedDateRecord(
        @Path("date") date: String,
    ): List<RecordingMetaData>

    @GET("/api/auth/script/{date}")
    suspend fun getSelectedDateRecordDetail(
        @Path("date") date: String,
        @Query("recordName") recordName: String,
    ): List<RecordingDetail>
}
