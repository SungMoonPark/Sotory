package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.creditcard.ConnectedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListWrapperResponse
import com.ssafy.sotory.domain.creditcard.ConnectedCardListModel
import com.ssafy.sotory.domain.creditcard.OwnedCardListModel
import com.ssafy.sotory.domain.creditcard.OwnedCardListWrapperModel

fun OwnedCardListResponse.toDomain(): OwnedCardListModel {
    return OwnedCardListModel(
        cardNo = cardNo,
        cardIssuerCode = cardIssuerCode,
        cardIssuerName = cardIssuerName,
        cardName = cardName
    )
}

fun OwnedCardListWrapperResponse.toDomain(): OwnedCardListWrapperModel {
    return OwnedCardListWrapperModel(
        data = data.map { it.toDomain() }
    )
}

fun ConnectedCardListResponse.toDomain(): ConnectedCardListModel {
    return ConnectedCardListModel(
        cardId = cardId,
        cardNo = cardNo,
        cardIssuerCode = cardIssuerCode,
        cardIssuerName = cardIssuerName,
        cardName = cardName
    )
}