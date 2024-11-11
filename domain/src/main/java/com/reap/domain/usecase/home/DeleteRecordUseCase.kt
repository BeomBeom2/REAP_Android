package com.reap.domain.usecase.home

import com.reap.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteRecordUseCase @Inject constructor(
    private val homeRepository : HomeRepository
) {
    suspend operator fun invoke(date : String, fileName : String, recordId : String) {
        return homeRepository.deleteRecord(date, fileName, recordId)
    }
}