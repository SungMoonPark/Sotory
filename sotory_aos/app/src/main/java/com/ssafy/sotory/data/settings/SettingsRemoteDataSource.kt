package com.ssafy.sotory.data.settings

import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.settings.AppLogOutRequest
import com.ssafy.sotory.data.dto.settings.SettingsAlarmRequest
import javax.inject.Inject

data class SettingsRemoteDataSource @Inject constructor(
    private val settingsSerVice: SettingsService
): SettingsDataSource {
    override suspend fun patchAlarmSettings(request: SettingsAlarmRequest): ResponseResult<Unit> =
        handleApiResponse {
            settingsSerVice.patchAlarmSettings(request)
        }

    override suspend fun postAppLogOut(request: AppLogOutRequest): ResponseResult<Unit> {
        val response = settingsSerVice.appLogOut(request)

        return if (response.isSuccessful) {
            ResponseResult.Success(Unit)
        } else {
            val rawError = response.errorBody()?.string()
            val message = rawError ?: "알 수 없는 오류"
            ResponseResult.Exception(
                Exception("로그아웃 실패: $message"),
                "로그아웃 중 오류 발생"
            )
        }
    }

}
