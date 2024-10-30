package com.reap.domain.usecase.SelectedDateRecord

import com.reap.domain.model.RecordingDetail
import com.reap.domain.repository.SelectedDateRecordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSelectedDateRecordDetailUseCase @Inject constructor(
    private val selectedDateRecordRepository : SelectedDateRecordRepository
) {
    suspend operator fun invoke(date : String, fileName : String) : List<RecordingDetail> {
        return selectedDateRecordRepository.getSelectedDateRecordDetailUseCase(date, fileName)
    }
}