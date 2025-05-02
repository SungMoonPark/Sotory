package com.ssafy.sotory.domain.myroom

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.data.dto.myroom.ReportRequest

interface MyRoomRepository {

    suspend fun patchBudget(
        request: BudgetUpdateRequest
    ): ResponseResult<Unit>

    suspend fun getBudget(
    ): ResponseResult<BudgetModel>

    suspend fun getReport(
        year: Int,
        month: Int
    ): ResponseResult<MyRoomModel>
}