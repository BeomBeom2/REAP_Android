package com.reap.domain.repository

import com.reap.domain.model.RecordingDetailData
import com.reap.domain.model.RecordingMetaData

interface SelectedDateRecordRepository {
    suspend fun getSelectedDateRecordUseCase(date : String) : List<RecordingMetaData>
    suspend fun getSelectedDateRecordDetailUseCase(recordedDate : String, fileName : String) : RecordingDetailData
}