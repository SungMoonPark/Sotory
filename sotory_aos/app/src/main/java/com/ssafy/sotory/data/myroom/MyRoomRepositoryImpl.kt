package com.ssafy.sotory.data.myroom

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.domain.myroom.BudgetModel
import com.ssafy.sotory.domain.myroom.MyRoomModel
import com.ssafy.sotory.domain.myroom.MyRoomRepository
import okhttp3.Response
import javax.inject.Inject

class MyRoomRepositoryImpl @Inject constructor(
    private val myRoomDataSource: MyRoomDataSource,
) : MyRoomRepository {

    override suspend fun patchBudget(request: BudgetUpdateRequest): ResponseResult<Unit> {
        return when (val result = myRoomDataSource.patchBudget(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    override suspend fun getBudget(): ResponseResult<BudgetModel> {
        return when (val result = myRoomDataSource.getBudget()) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    override suspend fun getReport(year: Int, month: Int): ResponseResult<MyRoomModel> {
        return when (val result = myRoomDataSource.getReport(year, month)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    companion object {
        private const val EXCEPTION_NETWORK_ERROR_MESSAGE =
            "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
    }

}