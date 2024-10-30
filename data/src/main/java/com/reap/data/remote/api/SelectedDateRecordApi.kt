package com.reap.data.remote.api

import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SelectedDateRecordApi {
    @GET("/auth/api/detail/{recordedDate}/record-script")
    suspend fun getSelectedDateRecord(
        @Path("recordedDate") recordedDate: String,
    ): List<RecordingMetaData>

    @GET("/auth/api/detail/{recordedDate}/total-script")
    suspend fun getSelectedDateRecordDetail(
        @Path("recordedDate") recordedDate: String,
        @Query("recordName") recordName: String,
    ): List<RecordingDetail>
}
