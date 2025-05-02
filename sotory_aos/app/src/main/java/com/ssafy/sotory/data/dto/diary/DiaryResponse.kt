package com.ssafy.sotory.data.dto.diary

import kotlinx.serialization.Serializable

@Serializable
enum class Weather {
    DEFAULT, RAIN, SNOW
}

@Serializable
data class DiaryResponse(
    val date: Int,
    val diaryCardId: String,
    val imgSrc: String,
    val summary: String,
    val weather: Weather,
)