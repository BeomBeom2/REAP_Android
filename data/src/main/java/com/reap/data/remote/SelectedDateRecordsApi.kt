package com.reap.data.remote

import com.reap.domain.model.RecognizeResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

//GET/audio/api/detail/{userid}/{recordedDate}/record-script
interface SelectedDateRecordsApi {
    @Multipart
    @POST("/audio/api/detail/{userid}/{recordedDate}/record-script")
    suspend fun postRecognizeUrl(
        @Part("userid") userid: String,
        @Part("recordedDate") recordedDate: String,
    ): RecognizeResponse
}
