package com.ssafy.sotory.domain.payment

import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest

data class PaymentUIState(
    val categoryName: String,
    val merchantName: String,
    val transactionBalance: String,
    val transactionTime: String,
    val enabled: Boolean = false,
)

fun PaymentUIState.toCreateRequest() = PaymentsCreateRequest(
    categoryName = categoryName,
    merchantName = merchantName,
    transactionBalance = transactionBalance,
    transactionTime = transactionTime,
)

fun PaymentUIState.toPatchRequest() = PaymentsUpdateRequest(
    transactionBalance = transactionBalance,
    categoryName = categoryName,
    merchantName = merchantName,
    transactionTime = transactionTime,
)