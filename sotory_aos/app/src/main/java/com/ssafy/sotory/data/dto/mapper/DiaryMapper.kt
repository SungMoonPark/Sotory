package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.diary.DiaryMonthListResponse
import com.ssafy.sotory.data.dto.diary.DiaryResponse
import com.ssafy.sotory.data.dto.diary.OtherDiaryResponse
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.domain.diary.DiaryMonthModel
import com.ssafy.sotory.domain.diary.OtherDiaryModel

fun DiaryResponse.toDomain(): DiaryModel {
    return DiaryModel(
        date = date,
        diaryCardId = diaryCardId,
        imgSrc = imgSrc,
        summary = summary,
        weather = weather,
    )
}

fun DiaryMonthListResponse.toDomain(): DiaryMonthModel {
    return DiaryMonthModel(
        month = month,
        count = count,
        diaries = diaries.map { it.toDomain() },
    )
}

fun OtherDiaryResponse.toDomain(): OtherDiaryModel {
    return OtherDiaryModel(
        imgSrc = imgSrc,
    )
}
