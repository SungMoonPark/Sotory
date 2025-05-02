package com.ssafy.sotory.domain.auth

data class AccountVerificationModel(
    val transactionUniqueNo: Long,
    val accountNo: String
)