package com.reap.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import com.reap.domain.usecase.home.DeleteRecordUseCase
import com.reap.domain.usecase.home.GetHomeRecentlyRecodingDataUseCase
import com.reap.domain.usecase.home.PutUpdateTopicAndFileNameUseCase
import com.reap.domain.usecase.selectedDateRecord.GetSelectedDateRecordDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeRecentlyRecodingDataUseCase: GetHomeRecentlyRecodingDataUseCase,
    private val getSelectedDateRecordDetailUseCase: GetSelectedDateRecordDetailUseCase,
    private val putUpdateTopicAnfFileNameUseCase : PutUpdateTopicAndFileNameUseCase,
    private val deleteRecordUseCase : DeleteRecordUseCase
) : ViewModel() {
    private val _recentlyRecordings = MutableStateFlow<List<RecordingMetaData>>(emptyList())
    val recentlyRecordings: StateFlow<List<RecordingMetaData>> = _recentlyRecordings.asStateFlow()
    private val _isFetchData = MutableStateFlow(false)
    val isFetchData: StateFlow<Boolean> = _isFetchData
    private val _selectedRecordingDetails = MutableStateFlow<List<RecordingDetail>?>(null)
    val selectedRecordingDetails: StateFlow<List<RecordingDetail>?> = _selectedRecordingDetails

    private val _selectDate = MutableStateFlow<String?>(null)
    val selectDate : MutableStateFlow<String?> = _selectDate

    fun getHomeRecentlyRecodingData() {
        viewModelScope.launch {
            try {
                val response = getHomeRecentlyRecodingDataUseCase()

                val recordingData = response.map {
                    RecordingMetaData(
                        recordId = it.recordId,
                        fileName = it.fileName,
                        uploadedDate = it.uploadedDate,
                        recordedDate = it.recordedDate,
                        topic = it.topic,
                        uploadedTime = it.uploadedTime
                    )
                }
                _recentlyRecordings.value = recordingData

            } catch (e: Exception) {
                Log.e("HomeViewModel", "From getHomeRecentlyRecodingData, Err is ${e.message}")
            }
        }
    }

    fun deleteRecord(date : String, fileName : String, recordId : String) {
        viewModelScope.launch{
            try{
                deleteRecordUseCase(date, fileName, recordId)
                getHomeRecentlyRecodingData()
            } catch(e : Exception) {
                Log.e("HomeViewModel", "From deleteRecord, Err is ${e.message}")
            }
        }
    }

    suspend fun updateTopicAndFileName(scriptId: String, newName: String, newTopic: String): Pair<String, String> {
        return try {
            val response = putUpdateTopicAnfFileNameUseCase(scriptId, newName, newTopic)
            getHomeRecentlyRecodingData()

            Pair(response.modifiedTopic, response.modifiedRecordName)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "From updateTopicAndFileName, Err is ${e.message}")
            Pair("", "")
        }
    }

    fun fetchRecordingDetails(selectedDate: String, recordingId: String) {
        viewModelScope.launch {
            try {
                val details = getSelectedDateRecordDetailUseCase(selectedDate, recordingId)
                _selectedRecordingDetails.value = details
                _selectDate.value = selectedDate
                _isFetchData.value = true
            } catch (e: Exception) {
                Log.e("SelectedDateRecordViewModel", "From fetchRecordingDetails, Err is ${e.message}")
            }
        }
    }

    fun resetToList() {
        _isFetchData.value = false
    }
}

