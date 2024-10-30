package com.reap.domain.repository

import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData

interface SelectedDateRecordRepository {
    suspend fun getSelectedDateRecordUseCase(date : String) : List<RecordingMetaData>
    suspend fun getSelectedDateRecordDetailUseCase(recordedDate : String, fileName : String) : List<RecordingDetail>
}