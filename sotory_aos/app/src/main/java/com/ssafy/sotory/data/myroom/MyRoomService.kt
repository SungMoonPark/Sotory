package com.ssafy.sotory.data.myroom

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.myroom.BudgetResponse
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.data.dto.myroom.ReportRequest
import com.ssafy.sotory.data.dto.myroom.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface MyRoomService {

    // 이번 달 예산 수정
    @PATCH(BUDGET_PATH)
    suspend fun patchBudget(
        @Body request: BudgetUpdateRequest,
    ): Response<BaseResponse<Unit>>

    // 이번 달 예산 조회
    @GET(BUDGET_PATH)
    suspend fun getBudget(
    ): Response<BaseResponse<BudgetResponse>>

    // 레포트 조회
    @GET(MYROOM_PATH)
    suspend fun getReport(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<BaseResponse<ReportResponse>>
//    ): BaseResponse<ReportResponse>

    companion object {
        private const val MYROOM_PATH = "/reports"
        private const val BUDGET_PATH = "/budgets"
    }
}