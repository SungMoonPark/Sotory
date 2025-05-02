package com.ssafy.sotory.data.dto.payment

import kotlinx.serialization.Serializable


@Serializable
data class DairyPaymentsListResponse(
    val paymentDiaryId: String,
    val categoryName: String,
    val diary: String?,
    val merchantName: String,
    val paymentId: String,
    val transactionBalance: String,
    val transactionTime: String,
    val isUserAdded: Boolean,
)