package com.reap.domain.usecase.SelectedDateRecord

import com.reap.domain.model.RecordingMetaData
import com.reap.domain.repository.SelectedDateRecordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSelectedDateRecordUseCase @Inject constructor(
    private val selectedDateRecordRepository : SelectedDateRecordRepository
) {
    suspend operator fun invoke(date : String) : List<RecordingMetaData> {
        return selectedDateRecordRepository.getSelectedDateRecordUseCase(date)
    }
}