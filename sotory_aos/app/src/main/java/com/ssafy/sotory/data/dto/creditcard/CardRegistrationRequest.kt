package com.ssafy.sotory.data.dto.creditcard

import kotlinx.serialization.Serializable

@Serializable
data class CardRegistrationRequest(
    val cardNo: String,
    val cardIssuerCode: String,
    val cardIssuerName: String,
    val cardName: String
)
