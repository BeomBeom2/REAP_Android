package com.reap.data.repository

import com.reap.data.remote.api.LoginApi
import com.reap.domain.model.AccessTokenResponse
import com.reap.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi : LoginApi
) : LoginRepository {
    override suspend fun getAccessToken(token: String): AccessTokenResponse {
        return loginApi.getAccessToken(token)
    }
}
