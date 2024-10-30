package com.reap.presentation.ui.selectedDateRecord

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.model.RecordingDetail
import com.reap.domain.model.RecordingMetaData
import com.reap.domain.usecase.SelectedDateRecord.GetSelectedDateRecordDetailUseCase
import com.reap.domain.usecase.SelectedDateRecord.GetSelectedDateRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedDateRecordViewModel @Inject constructor(
    private val getSelectedDateRecordUseCase: GetSelectedDateRecordUseCase,
    private val getSelectedDateRecordDetailUseCase: GetSelectedDateRecordDetailUseCase
) : ViewModel() {

    // StateFlow를 리스트 형식으로 변경
    private val _selectedDateRecordData = MutableStateFlow<List<RecordingMetaData>>(emptyList())
    val selectedDateRecordData: StateFlow<List<RecordingMetaData>> = _selectedDateRecordData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedRecordingDetails = MutableStateFlow<List<RecordingDetail>?>(null)
    val selectedRecordingDetails: StateFlow<List<RecordingDetail>?> = _selectedRecordingDetails

    private val _screenState = MutableStateFlow(ScreenState.RECORD_LIST)
    val screenState: StateFlow<ScreenState> = _screenState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getSelectedDateRecordData(date : String) {
        viewModelScope.launch {
            try {
                // UseCase를 호출하여 데이터를 가져옴
                val response = getSelectedDateRecordUseCase(date)

                // JSON 응답을 RecentlyRecording 데이터 클래스로 매핑
                val recordingData = response.map {
                    RecordingMetaData(
                        fileName = it.fileName,
                        uploadedDate = it.uploadedDate,
                        recordedDate = it.recordedDate,
                        topic = it.topic ?: "[강의]",
                        uploadedTime = it.uploadedTime
                    )
                }

                // 리스트 데이터를 StateFlow에 반영
                _selectedDateRecordData.value = recordingData

            } catch (e: Exception) {
                // 오류 처리
                Log.e("SelectedDateRecordViewModel", "Error fetching recording data: ${e.message}")
            }
        }
    }

    fun fetchRecordingDetails(selectedDate: String, recordingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val details = getSelectedDateRecordDetailUseCase(selectedDate, recordingId)
                _selectedRecordingDetails.value = details
                _screenState.value = ScreenState.RECORD_SCRIPT
            } catch (e: Exception) {
                _screenState.value = ScreenState.RECORD_ERROR
                _errorMessage.value = "불러오기 실패"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetToList() {
        _screenState.value = ScreenState.RECORD_LIST
        _selectedRecordingDetails.value = null
    }
}

enum class ScreenState {
    RECORD_LIST,
    RECORD_SCRIPT,
    RECORD_ERROR
}