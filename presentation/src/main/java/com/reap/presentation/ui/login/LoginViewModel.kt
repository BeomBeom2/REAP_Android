package com.reap.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.model.AccessTokenResponse
import com.reap.domain.usecase.login.GetAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase
) : ViewModel() {

    private val _accessToken = MutableStateFlow<AccessTokenResponse?>(null)
    val accessToken: StateFlow<AccessTokenResponse?> get() = _accessToken

    fun getAccessToken(token: String) {
        viewModelScope.launch {
            try {
                val response = getAccessTokenUseCase(token)

                _accessToken.value = response
            } catch (e: Exception) {
                // 에러 처리
                Log.e("LoginViewModel", "Error getting access token", e)
            }
        }
    }
}


