package com.ssafy.sotory.domain.creditcard

data class ConnectedCardListModel(
    val cardId: String,
    val cardNo: String,
    val cardIssuerCode: String,
    val cardIssuerName: String,
    val cardName: String
)
