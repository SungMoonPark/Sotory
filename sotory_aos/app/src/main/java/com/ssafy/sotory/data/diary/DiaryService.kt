package com.ssafy.sotory.data.diary

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.diary.DiaryMonthListResponse
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.data.dto.diary.DiaryResponse
import com.ssafy.sotory.data.dto.diary.OtherDiaryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {
    /// 소비 일기 작성 및 수정
    @PATCH(DIARY_MODIFY_PATH)
    suspend fun patchDiary(
        @Path("paymentDiaryId") paymentDiaryId: String,
        @Body request: DiaryPaymentUpdateRequest,
    ): BaseResponse<Unit>

    /// 소비 일기 삭제
    @DELETE(DIARY_MODIFY_PATH)
    suspend fun deleteDiary(
        @Path("paymentDiaryId") paymentDiaryId: String,
    ): BaseResponse<Unit>

    /// 모든(월) 일기 카드 전체 조회
    @GET(DIARY_GET_PATH)
    suspend fun getDiaries(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): BaseResponse<List<DiaryResponse>>

    /// 일기 카드 월별 조회
    @GET(DIARY_MONTH_PATH)
    suspend fun getMonthDiaries(
        @Query("year") year: Int,
    ): BaseResponse<List<DiaryMonthListResponse>>

    /// 일기 카드 단건 조회
    @GET(DIARY_SINGLE_PATH)
    suspend fun getSingleDiary(
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
    ): BaseResponse<DiaryResponse>

    /// 일기 카드 생성
    @POST(DIARY_CREATE_PATH)
    suspend fun postDiary(
    ): BaseResponse<DiaryResponse>

    /// 일기 카드 랜덤 조회 (10개)
    @GET(DIARY_PATH)
    suspend fun getOtherDiaries(
    ): OtherDiaryResponse

    companion object {
        private const val DIARY_MODIFY_PATH = "/diaries/{paymentDiaryId}"
        private const val DIARY_GET_PATH = "/diaries"
        private const val DIARY_MONTH_PATH = "/diaries/monthly-preview"
        private const val DIARY_SINGLE_PATH = "/diaries/{year}/{month}/{day}"
        private const val DIARY_CREATE_PATH = "/diary/generation/card"
        private const val DIARY_PATH = "/diary"


    }
}