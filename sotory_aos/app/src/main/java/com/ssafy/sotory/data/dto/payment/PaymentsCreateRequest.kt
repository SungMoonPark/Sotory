package com.ssafy.sotory.data.dto.payment

import com.ssafy.sotory.domain.payment.PaymentModel
import kotlinx.serialization.Serializable

@Serializable
data class PaymentsCreateRequest(
    val merchantName: String,
    val categoryName: String,
    val transactionBalance: String,
    val transactionTime: String,
)

fun PaymentsCreateRequest.toModel(paymentId: String, paymentDiaryId: String): PaymentModel {
    return PaymentModel(
        paymentDiaryId = paymentDiaryId,
        categoryName = categoryName,
        diary = null,
        merchantName = merchantName,
        paymentId = paymentId,
        transactionBalance = transactionBalance,
        transactionTime = transactionTime,
        isUserAdded = true,
    )
}