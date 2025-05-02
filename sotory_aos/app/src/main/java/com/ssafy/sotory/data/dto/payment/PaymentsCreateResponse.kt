package com.ssafy.sotory.data.dto.payment

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsCreateResponse(val paymentsId: String, val paymentsDiaryId: String)