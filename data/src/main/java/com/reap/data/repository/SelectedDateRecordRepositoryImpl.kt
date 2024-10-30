package com.reap.data.repository

import com.reap.data.remote.api.SelectedDateRecordApi
import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import com.reap.domain.repository.SelectedDateRecordRepository
import javax.inject.Inject

class SelectedDateRecordRepositoryImpl @Inject constructor(
    private val selectedDateRecordApi : SelectedDateRecordApi
) : SelectedDateRecordRepository {
    override suspend fun getSelectedDateRecordUseCase(date : String) : List<RecordingMetaData> {
        return selectedDateRecordApi.getSelectedDateRecord(date)
    }

    override suspend fun getSelectedDateRecordDetailUseCase(recordedDate : String, fileName : String) : List<RecordingDetail> {
        return selectedDateRecordApi.getSelectedDateRecordDetail(recordedDate, fileName)
    }
}
