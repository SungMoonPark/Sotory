package com.ssafy.sotory.data.dto.diary

import kotlinx.serialization.Serializable

@Serializable
data class OtherDiaryResponse(
    val imgSrc: MutableList<String>,
)