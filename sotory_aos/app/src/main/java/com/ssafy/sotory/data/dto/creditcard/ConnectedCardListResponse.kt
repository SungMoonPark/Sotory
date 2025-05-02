package com.ssafy.sotory.data.dto.creditcard

import kotlinx.serialization.Serializable

@Serializable
data class ConnectedCardListResponse(
    val cardId: String,
    val cardNo: String,
    val cardIssuerCode: String,
    val cardIssuerName: String,
    val cardName: String
)
