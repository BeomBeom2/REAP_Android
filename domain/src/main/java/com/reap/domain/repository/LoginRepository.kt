package com.reap.domain.repository

import com.reap.domain.model.AccessTokenResponse

interface LoginRepository {
    suspend fun getAccessToken(token: String) : AccessTokenResponse
}