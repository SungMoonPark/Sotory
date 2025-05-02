//package com.ssafy.sotory.data.diary
//
//import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
//import com.ssafy.sotory.data.ResponseResult
//import com.ssafy.sotory.data.dto.diary.DiaryListResponse
//import com.ssafy.sotory.data.dto.diary.DiaryMonthListResponse
//import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
//import com.ssafy.sotory.data.dto.payment.DairyPaymentsListResponse
//import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
//import com.ssafy.sotory.data.dto.payment.PaymentsCreateResponse
//import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest
//import javax.inject.Inject
//
//class DiaryRemoteDataSource @Inject constructor(private val diaryService: DiaryService) :
//    DiaryDataSource {
//    override suspend fun patchDiary(
//        paymentId: String,
//        request: DiaryPaymentUpdateRequest,
//    ): ResponseResult<Unit> = handleApiResponse {
//        diaryService.patchDiary(paymentId, request)
//    }
//
//    override suspend fun deleteDiary(paymentId: String): ResponseResult<Unit> = handleApiResponse {
//        diaryService.deleteDiary(paymentId)
//    }
//
//    override suspend fun getDiaries(
//        year: String,
//        month: String,
//    ): ResponseResult<List<DiaryListResponse>> =
//        handleApiResponse {
//            diaryService.getDiaries(year, month)
//        }
//
//    override suspend fun getMonthDiaries(): ResponseResult<List<DiaryMonthListResponse>> =
//        handleApiResponse {
//            diaryService.getMonthDiaries()
//        }
//
//}