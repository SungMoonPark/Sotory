package com.ssafy.sotory.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenDataResponse(
    val accessToken: String,
    val refreshToken: String
)
