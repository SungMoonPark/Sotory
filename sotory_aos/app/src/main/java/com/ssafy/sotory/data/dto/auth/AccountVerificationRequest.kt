package com.ssafy.sotory.data.dto.auth

data class AccountVerificationRequest(
    val accountNo: String,
    val authText: String
)
