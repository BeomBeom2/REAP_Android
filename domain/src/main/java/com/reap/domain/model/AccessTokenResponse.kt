package com.reap.domain.model

data class AccessTokenResponse(
    val success : Boolean,
    val jwtToken : String
)
