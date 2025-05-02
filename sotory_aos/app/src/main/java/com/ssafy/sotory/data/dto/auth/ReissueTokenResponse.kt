package com.ssafy.sotory.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
