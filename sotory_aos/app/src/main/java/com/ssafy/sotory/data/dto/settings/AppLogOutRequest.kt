package com.ssafy.sotory.data.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class AppLogOutRequest(
    val refreshToken: String
)
