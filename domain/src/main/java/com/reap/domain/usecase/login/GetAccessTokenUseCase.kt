package com.reap.domain.usecase.login

import com.reap.domain.model.AccessTokenResponse
import com.reap.domain.repository.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAccessTokenUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(token: String) : AccessTokenResponse {
        return loginRepository.getAccessToken(token)
    }
}