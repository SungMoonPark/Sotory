package com.ssafy.sotory.domain.payment

data class PaymentListModel(
    val paymentDiaires: List<PaymentModel>,
    val isCardCreated: Boolean,
)
