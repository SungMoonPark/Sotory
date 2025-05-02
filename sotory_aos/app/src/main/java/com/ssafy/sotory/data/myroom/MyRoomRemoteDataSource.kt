package com.ssafy.sotory.data.myroom

import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.myroom.BudgetResponse
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.data.dto.myroom.ReportRequest
import com.ssafy.sotory.data.dto.myroom.ReportResponse
import javax.inject.Inject

data class MyRoomRemoteDataSource @Inject constructor(private val myRoomService: MyRoomService) :
    MyRoomDataSource {
    override suspend fun patchBudget(request: BudgetUpdateRequest): ResponseResult<Unit> =
        handleApiResponse {
            myRoomService.patchBudget(request)
        }

    override suspend fun getBudget(): ResponseResult<BudgetResponse> =
        handleApiResponse {
            myRoomService.getBudget()
        }

//    override suspend fun getReport(year: Int, month: Int): ResponseResult<ReportResponse> =
//        handleApiResponse {
//            myRoomService.getReport(year = year, month = month)
//        }

    override suspend fun getReport(year: Int, month: Int): ResponseResult<ReportResponse> {
        val response = myRoomService.getReport(year, month)

        return if (response.isSuccessful) {
            val reportData = response.body()?.data
            if (reportData != null) {
                ResponseResult.Success(reportData)
            } else {
                ResponseResult.Exception(
                    Exception("응답 바디가 null입니다."),
                    "리포트 불러오기 실패"
                )
            }
        } else {
            val rawError = response.errorBody()?.string()
            val message = rawError ?: "알 수 없는 오류"
            ResponseResult.Exception(
                Exception("로그아웃 실패: $message"),
                "로그아웃 중오류 발생"
            )
        }
    }

}