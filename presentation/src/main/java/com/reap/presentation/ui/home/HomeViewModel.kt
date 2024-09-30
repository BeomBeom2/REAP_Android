package com.reap.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.model.RecentlyRecording
import com.reap.domain.usecase.home.GetHomeRecentlyRecodingDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeRecentlyRecodingDataUseCase: GetHomeRecentlyRecodingDataUseCase
) : ViewModel() {

    // StateFlow를 리스트 형식으로 변경
    private val _recentlyRecordingData = MutableStateFlow<List<RecentlyRecording>>(emptyList())
    val recentlyRecordingData: StateFlow<List<RecentlyRecording>> = _recentlyRecordingData.asStateFlow()

    fun getHomeRecentlyRecodingData() {
        viewModelScope.launch {
            try {
                // UseCase를 호출하여 데이터를 가져옴
                val response = getHomeRecentlyRecodingDataUseCase.invoke("test1")

                // JSON 응답을 RecentlyRecording 데이터 클래스로 매핑
                val recordingData = response.map {
                    RecentlyRecording(
                        fileName = it.fileName,
                        uploadedDate = it.uploadedDate,
                        recordedDate = it.recordedDate,
                        topic = it.topic ?: "[강의]",
                        uploadedTime = it.uploadedTime
                    )
                }

                // 리스트 데이터를 StateFlow에 반영
                _recentlyRecordingData.value = recordingData

            } catch (e: Exception) {
                // 오류 처리
                Log.e("HomeViewModel", "Error fetching recording data: ${e.message}")
            }
        }
    }
}
