package com.ssafy.sotory.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenRequest(
    val refreshToken: String
)
