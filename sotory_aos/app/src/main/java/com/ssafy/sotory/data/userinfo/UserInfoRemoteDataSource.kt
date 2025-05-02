package com.ssafy.sotory.data.userinfo

import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest
import javax.inject.Inject

data class UserInfoRemoteDataSource @Inject constructor(private val userInfoService: UserInfoService) :
    UserInfoDataSource {

    override suspend fun getUserInfo(): ResponseResult<UserInfoResponse> =
        handleApiResponse {
            userInfoService.getUserInfo()
        }

    override suspend fun patchNickname(request: UserInfoUpdateNicknameRequest): ResponseResult<Unit> =
        handleApiResponse {
            userInfoService.patchNickname(request)
        }
}
