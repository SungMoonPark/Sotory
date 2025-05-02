package com.ssafy.sotory.data.dto.diary

import kotlinx.serialization.Serializable

@Serializable
data class DiaryCreateResponse(
    val summary: String,
    val img_src: String,
)