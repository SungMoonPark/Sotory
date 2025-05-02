package com.ssafy.sotory.domain.diary

data class DiaryMonthModel(
    val month: Int,
    val count: Int,
    val diaries: List<DiaryModel>,
)