package com.ssafy.sotory.domain.userinfo

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest

interface UserInfoRepository {

    suspend fun getUserInfo(
    ): ResponseResult<UserInfoModel>

    suspend fun patchNickname(
        request: UserInfoUpdateNicknameRequest
    ): ResponseResult<Unit>
}