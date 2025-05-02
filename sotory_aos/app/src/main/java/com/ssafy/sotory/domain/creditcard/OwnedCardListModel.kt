package com.ssafy.sotory.domain.creditcard

data class OwnedCardListWrapperModel(
    val data: List<OwnedCardListModel>
)

data class OwnedCardListModel(
    val cardNo: String,
    val cardIssuerCode: String,
    val cardIssuerName: String,
    val cardName: String
)
