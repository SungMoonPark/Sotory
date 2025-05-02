package com.ssafy.sotory.data.userinfo

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserInfoService {

    @GET(USER_INFO_PATH)
    suspend fun getUserInfo(
    ): Response<BaseResponse<UserInfoResponse>>

    @PATCH(USER_INFO_PATH)
    suspend fun patchNickname(
        @Body request: UserInfoUpdateNicknameRequest
    ): Response<BaseResponse<Unit>>

    companion object {
        private const val USER_INFO_PATH = "users"
    }
}