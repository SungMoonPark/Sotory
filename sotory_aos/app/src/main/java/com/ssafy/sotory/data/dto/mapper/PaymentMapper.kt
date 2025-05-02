package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.payment.DairyPaymentsListResponse
import com.ssafy.sotory.data.dto.payment.PaymentsCreateResponse
import com.ssafy.sotory.domain.payment.PaymentCreateModel
import com.ssafy.sotory.domain.payment.PaymentModel


fun DairyPaymentsListResponse.toDomain(): PaymentModel {
    return PaymentModel(
        paymentDiaryId = paymentDiaryId,
        categoryName = categoryName,
        diary = diary,
        merchantName = merchantName,
        paymentId = paymentId,
        transactionBalance = transactionBalance,
        transactionTime = transactionTime,
        isUserAdded = isUserAdded
    )
}

fun PaymentsCreateResponse.toDomain(): PaymentCreateModel {
    return PaymentCreateModel(
        paymentId = paymentsId,
        paymentsDiaryId = paymentsDiaryId,
    )
}



