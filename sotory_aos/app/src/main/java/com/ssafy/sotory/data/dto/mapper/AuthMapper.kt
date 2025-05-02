package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.auth.AccountVerificationResponse
import com.ssafy.sotory.data.dto.auth.ReissueTokenResponse
import com.ssafy.sotory.data.dto.auth.TokenDataResponse
import com.ssafy.sotory.domain.auth.AccountVerificationModel
import com.ssafy.sotory.domain.auth.ReissueTokenModel
import com.ssafy.sotory.domain.auth.TokenDataModel

fun TokenDataResponse.toDomain(): TokenDataModel {
    return TokenDataModel(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun ReissueTokenResponse.toDomain(): ReissueTokenModel {
    return ReissueTokenModel(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun AccountVerificationResponse.toDomain(): AccountVerificationModel {
    return AccountVerificationModel(
        transactionUniqueNo = transactionUniqueNo,
        accountNo = accountNo
    )
}