package com.ssafy.sotory.data.creditcard

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest
import com.ssafy.sotory.data.dto.creditcard.ConnectedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListWrapperResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CreditCardService {

    @GET(MY_OWNED_CARD_PATH)
    suspend fun getMyOwnedCard(
    ): Response<BaseResponse<OwnedCardListWrapperResponse>>

    @GET(MY_CONNECTED_CARD_PATH)
    suspend fun getMyConnectedCard(
    ): Response<BaseResponse<List<ConnectedCardListResponse>>>

    @POST(CARD_REGISTER_PATH)
    suspend fun postCard(
        @Body request: List<CardRegistrationRequest>
    ): Response<BaseResponse<Unit>>

    @DELETE(CARD_DELETE_PATH)
    suspend fun deleteCard(
        @Path("cardId") cardId: String
    ): Response<BaseResponse<Unit>>


    companion object {
        private const val MY_OWNED_CARD_PATH = "/cards/owned"
        private const val MY_CONNECTED_CARD_PATH = "/cards/connected"
        private const val CARD_REGISTER_PATH = "/cards"
        private const val CARD_DELETE_PATH = "/cards/{cardId}"
    }
}