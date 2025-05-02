package com.ssafy.sotory.domain.creditcard

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest

interface CreditCardRepository {

    suspend fun getMyOwnedCard(
    ): ResponseResult<OwnedCardListWrapperModel>

    suspend fun getMyConnectedCard(
    ): ResponseResult<List<ConnectedCardListModel>>

    suspend fun postCard(
        request: List<CardRegistrationRequest>
    ): ResponseResult<Unit>

    suspend fun deleteCard(
        cardId: String
    ): ResponseResult<Unit>
}