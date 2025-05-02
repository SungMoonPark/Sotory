package com.ssafy.sotory.data.dto.creditcard

import kotlinx.serialization.Serializable

@Serializable
data class OwnedCardListWrapperResponse(
    val data: List<OwnedCardListResponse>
)

@Serializable
data class OwnedCardListResponse(
    val cardNo: String,
    val cardIssuerCode: String,
    val cardIssuerName: String,
    val cardName: String
)