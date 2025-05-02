package com.ssafy.sotory.domain.auth

data class ReissueTokenModel(
    val accessToken: String,
    val refreshToken: String
)
