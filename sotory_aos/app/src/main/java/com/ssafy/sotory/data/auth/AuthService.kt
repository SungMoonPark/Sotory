package com.ssafy.sotory.data.auth

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.auth.AccountVerificationRequest
import com.ssafy.sotory.data.dto.auth.AccountVerificationResponse
import com.ssafy.sotory.data.dto.auth.ReissueTokenRequest
import com.ssafy.sotory.data.dto.auth.ReissueTokenResponse
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest
import com.ssafy.sotory.data.dto.auth.TokenDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST(LOGIN_SIGNUP_PATH)
    suspend fun postSignUpLogin(
        @Body() request: SignUpLoginRequest
    ): Response<BaseResponse<TokenDataResponse>>

    @POST(REISSUE_TOKEN_PATH)
    suspend fun postReissueToken(
        @Body() request: ReissueTokenRequest
    ): Response<BaseResponse<ReissueTokenResponse>>

    @POST(VERIFICATION_PATH)
    suspend fun postAccountVerification(
        @Body() request: AccountVerificationRequest
    ): Response<BaseResponse<AccountVerificationResponse>>

    companion object {
        private const val LOGIN_SIGNUP_PATH = "auth/kakao"
        private const val REISSUE_TOKEN_PATH = "auth/reissue"
        private const val VERIFICATION_PATH = "account/1won-verification"
    }
}