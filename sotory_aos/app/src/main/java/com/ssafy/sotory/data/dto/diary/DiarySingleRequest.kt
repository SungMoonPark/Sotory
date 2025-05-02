package com.ssafy.sotory.data.dto.diary

import kotlinx.serialization.Serializable

@Serializable
data class DiarySingleRequest(
    val year: String,
    val month: String,
    val day: String,
)
