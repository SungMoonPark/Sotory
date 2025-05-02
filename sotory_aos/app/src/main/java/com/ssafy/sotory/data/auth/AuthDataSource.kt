package com.ssafy.sotory.data.auth

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.auth.AccountVerificationRequest
import com.ssafy.sotory.data.dto.auth.AccountVerificationResponse
import com.ssafy.sotory.data.dto.auth.ReissueTokenRequest
import com.ssafy.sotory.data.dto.auth.ReissueTokenResponse
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest
import com.ssafy.sotory.data.dto.auth.TokenDataResponse

interface AuthDataSource {

    suspend fun postSignUpLogin(
        request: SignUpLoginRequest
    ): ResponseResult<TokenDataResponse>

    suspend fun postReissueToken(
        request: ReissueTokenRequest
    ): ResponseResult<ReissueTokenResponse>

    suspend fun postAccountVerification(
        request: AccountVerificationRequest
    ): ResponseResult<AccountVerificationResponse>
}