package com.reap.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.usecase.chat.PostQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val postQuestionUseCase: PostQuestionUseCase,
    private val postQuestionStreamUseCase: PostQuestionUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 사용자가 메시지를 보낼 때 호출
    fun sendMessage(question: String) {
        viewModelScope.launch {
            val newMessage = Message(text = question, isFromUser = true)
            _messages.value += newMessage

            _isLoading.value = true

            val response = postQuestionUseCase(question)
            _isLoading.value = false

            response?.let { responseMessage ->
                val receivedMessage = Message(text = responseMessage, isFromUser = false)
                _messages.value += receivedMessage
            }
        }
    }
}

