package com.ssafy.sotory.data.settings

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.data.dto.settings.AppLogOutRequest
import com.ssafy.sotory.data.dto.settings.SettingsAlarmRequest
import com.ssafy.sotory.domain.settings.SettingsRepository
import javax.inject.Inject

data class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataSource: SettingsDataSource
): SettingsRepository {
    override suspend fun patchAlarmSettings(request: SettingsAlarmRequest): ResponseResult<Unit> {
        return when (val result = settingsDataSource.patchAlarmSettings(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    override suspend fun postAppLogOut(request: AppLogOutRequest): ResponseResult<Unit> {
        return when (val result = settingsDataSource.postAppLogOut(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    companion object {
        private const val EXCEPTION_NETWORK_ERROR_MESSAGE =
            "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
    }
}
