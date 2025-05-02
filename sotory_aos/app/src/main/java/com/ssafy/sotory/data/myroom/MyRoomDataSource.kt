package com.ssafy.sotory.data.myroom

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.myroom.BudgetResponse
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.data.dto.myroom.ReportRequest
import com.ssafy.sotory.data.dto.myroom.ReportResponse

interface MyRoomDataSource {

    suspend fun patchBudget(
        request: BudgetUpdateRequest
    ): ResponseResult<Unit>

    suspend fun getBudget(
    ): ResponseResult<BudgetResponse>

    suspend fun getReport(
        year: Int,
        month: Int
    ): ResponseResult<ReportResponse>
    
}