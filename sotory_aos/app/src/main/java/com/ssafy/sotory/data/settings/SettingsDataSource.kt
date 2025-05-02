package com.ssafy.sotory.data.settings

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.settings.AppLogOutRequest
import com.ssafy.sotory.data.dto.settings.SettingsAlarmRequest

interface SettingsDataSource {
    suspend fun patchAlarmSettings(
        request: SettingsAlarmRequest
    ): ResponseResult<Unit>

    suspend fun postAppLogOut(
        request: AppLogOutRequest
    ): ResponseResult<Unit>
}