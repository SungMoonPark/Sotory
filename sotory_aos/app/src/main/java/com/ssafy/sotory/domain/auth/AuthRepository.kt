package com.ssafy.sotory.domain.auth

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.auth.AccountVerificationRequest
import com.ssafy.sotory.data.dto.auth.ReissueTokenRequest
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest

interface AuthRepository {

    suspend fun postSignUpLogin(
        request: SignUpLoginRequest
    ): ResponseResult<TokenDataModel>

    suspend fun postReissueToken(
        request: ReissueTokenRequest
    ): ResponseResult<ReissueTokenModel>

    suspend fun postAccountVerification(
        request: AccountVerificationRequest
    ): ResponseResult<AccountVerificationModel>
}