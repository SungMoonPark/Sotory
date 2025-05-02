package com.ssafy.sotory.domain.diary

import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.data.dto.diary.DiarySingleRequest
import kotlinx.coroutines.flow.Flow


interface DiaryRepository {
    val dateDiaries: Flow<Map<Int, List<List<BaseDiaryModel>>>>
    val monthDiaries: Flow<List<DiaryMonthModel>>
    val otherDiaries: Flow<OtherDiaryModel>

    fun postDiary(): Flow<DiaryModel>

    /// 소비 일기 작성 및 수정
    fun patchDiary(
        paymentDiaryId: String,
        request: DiaryPaymentUpdateRequest,
    ): Flow<Unit>

    /// 소비 일기 삭제
    fun deleteDiary(
        paymentDiaryId: String,
    ): Flow<Unit>

    /// 일기 카드 전체 조회
    fun fetchDateDiaries(
        year: Int,
        month: Int,
    ): Flow<List<DiaryModel>>

    /// 일기 카드 월별 조회
    fun fetchMonthDiaries(year: Int): Flow<List<DiaryMonthModel>>

    fun fetchOtherDiaries(): Flow<OtherDiaryModel>

    /// 일기 카드 단건 조회
    fun getSingleDiary(request: DiarySingleRequest): Flow<DiaryModel>

    fun observeDateDiaries(): Flow<Map<Int, List<List<BaseDiaryModel>>>>

    fun observeMonthDiaries(): Flow<List<DiaryMonthModel>>

    fun observeOtherDiaries(): Flow<OtherDiaryModel>
}