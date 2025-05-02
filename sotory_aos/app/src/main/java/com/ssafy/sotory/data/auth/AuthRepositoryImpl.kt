package com.ssafy.sotory.data.auth

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.data.dto.auth.AccountVerificationRequest
import com.ssafy.sotory.data.dto.auth.ReissueTokenRequest
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest
import com.ssafy.sotory.domain.auth.AccountVerificationModel
import com.ssafy.sotory.domain.auth.AuthRepository
import com.ssafy.sotory.domain.auth.ReissueTokenModel
import com.ssafy.sotory.domain.auth.TokenDataModel
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun postSignUpLogin(request: SignUpLoginRequest): ResponseResult<TokenDataModel> {
        return when (val result = authDataSource.postSignUpLogin(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    override suspend fun postReissueToken(request: ReissueTokenRequest): ResponseResult<ReissueTokenModel> {
        return when (val result = authDataSource.postReissueToken(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    override suspend fun postAccountVerification(request: AccountVerificationRequest): ResponseResult<AccountVerificationModel> {
        return when (val result = authDataSource.postAccountVerification(request)) {
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