package com.ssafy.sotory.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpLoginRequest(
    val accessToken: String
)