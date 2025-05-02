package com.ssafy.sotory.domain.auth

data class TokenDataModel(
    val accessToken: String,
    val refreshToken: String
)
