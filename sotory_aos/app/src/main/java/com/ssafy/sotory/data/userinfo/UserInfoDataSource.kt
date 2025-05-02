package com.ssafy.sotory.data.userinfo

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest

interface UserInfoDataSource {
    suspend fun getUserInfo(
    ): ResponseResult<UserInfoResponse>

    suspend fun patchNickname(
        request: UserInfoUpdateNicknameRequest
    ): ResponseResult<Unit>
}