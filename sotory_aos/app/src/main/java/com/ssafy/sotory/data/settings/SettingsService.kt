package com.ssafy.sotory.data.settings

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.settings.AppLogOutRequest
import com.ssafy.sotory.data.dto.settings.SettingsAlarmRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface SettingsService {

    @PATCH(SETTING_ALARM_PATH)
    suspend fun patchAlarmSettings(
        @Body request: SettingsAlarmRequest
    ): Response<BaseResponse<Unit>>

    @POST(LOGOUT_PATH)
    suspend fun appLogOut(
        @Body request: AppLogOutRequest
    ): Response<BaseResponse<Unit>>

    companion object {
        private const val SETTING_ALARM_PATH = "notifications/settings"
        private const val LOGOUT_PATH = "auth/logout"
    }
}