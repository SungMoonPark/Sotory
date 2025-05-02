package com.ssafy.sotory.data.auth

import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.auth.AccountVerificationRequest
import com.ssafy.sotory.data.dto.auth.AccountVerificationResponse
import com.ssafy.sotory.data.dto.auth.ReissueTokenRequest
import com.ssafy.sotory.data.dto.auth.ReissueTokenResponse
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest
import com.ssafy.sotory.data.dto.auth.TokenDataResponse
import javax.inject.Inject

data class AuthRemoteDataSource @Inject constructor(private val authService: AuthService) :
    AuthDataSource {

    override suspend fun postSignUpLogin(request: SignUpLoginRequest): ResponseResult<TokenDataResponse> =
        handleApiResponse {
            authService.postSignUpLogin(request)
        }

    override suspend fun postReissueToken(request: ReissueTokenRequest): ResponseResult<ReissueTokenResponse> =
        handleApiResponse {
            authService.postReissueToken(request)
        }


    override suspend fun postAccountVerification(request: AccountVerificationRequest): ResponseResult<AccountVerificationResponse> =
        handleApiResponse {
            authService.postAccountVerification(request)
        }

}