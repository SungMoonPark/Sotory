package com.ssafy.sotory.data.creditcard

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest
import com.ssafy.sotory.data.dto.creditcard.ConnectedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListWrapperResponse

interface CreditCardDataSource {

    suspend fun getMyOwnedCard(
    ): ResponseResult<OwnedCardListWrapperResponse>

    suspend fun getMyConnectedCard(
    ): ResponseResult<List<ConnectedCardListResponse>>

    suspend fun postCard(
        request: List<CardRegistrationRequest>
    ): ResponseResult<Unit>

    suspend fun deleteCard(
        cardId: String
    ): ResponseResult<Unit>

}