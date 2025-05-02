package com.ssafy.sotory.data.dto.diary

import kotlinx.serialization.Serializable

@Serializable
data class  DiaryMonthListResponse(
    val month: Int,
    val count: Int,
    val diaries: List<DiaryResponse>,
)
