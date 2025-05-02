package com.ssafy.sotory.data.userinfo

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest
import com.ssafy.sotory.domain.userinfo.UserInfoModel
import com.ssafy.sotory.domain.userinfo.UserInfoRepository
import javax.inject.Inject

data class UserInfoRepositoryImpl @Inject constructor(
    private val userInfoDataSource: UserInfoDataSource
): UserInfoRepository {
    override suspend fun getUserInfo(): ResponseResult<UserInfoModel> {
        return when (val result = userInfoDataSource.getUserInfo()) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    override suspend fun patchNickname(request: UserInfoUpdateNicknameRequest): ResponseResult<Unit> {
        return when (val result = userInfoDataSource.patchNickname(request)) {
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
