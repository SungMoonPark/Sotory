package com.ssafy.sotory.data.diary

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.diary.DiaryResponse
import com.ssafy.sotory.data.dto.diary.DiaryMonthListResponse
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest

interface DiaryDataSource {
    /// 소비 일기 작성 및 수정
    suspend fun patchDiary(
        paymentId: String,
        request: DiaryPaymentUpdateRequest,
    ): ResponseResult<Unit>

    /// 소비 일기 삭제
    suspend fun deleteDiary(
        paymentId: String,
    ): ResponseResult<Unit>

    /// 일기 카드 전체 조회
    suspend fun getDiaries(
        year: String,
        month: String,
    ): ResponseResult<List<DiaryResponse>>

    /// 일기 카드 월별 조회
    suspend fun getMonthDiaries(
    ): ResponseResult<List<DiaryMonthListResponse>>
}
