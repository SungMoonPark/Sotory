package com.ssafy.sotory.data.creditcard

import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest
import com.ssafy.sotory.data.dto.creditcard.ConnectedCardListResponse
import com.ssafy.sotory.data.dto.creditcard.OwnedCardListWrapperResponse
import javax.inject.Inject

data class CreditCardRemoteDataSource @Inject constructor(private val creditCardService: CreditCardService) :
    CreditCardDataSource {
    override suspend fun getMyOwnedCard(): ResponseResult<OwnedCardListWrapperResponse> =
        handleApiResponse {
            creditCardService.getMyOwnedCard()
        }

    override suspend fun getMyConnectedCard(): ResponseResult<List<ConnectedCardListResponse>> =
        handleApiResponse {
            creditCardService.getMyConnectedCard()
        }

    override suspend fun postCard(request: List<CardRegistrationRequest>): ResponseResult<Unit> {
        val response = creditCardService.postCard(request)

        return if (response.isSuccessful) {
            ResponseResult.Success(Unit)
        } else {
            val rawError = response.errorBody()?.string()
            val message = rawError ?: "알 수 없는 오류"
            ResponseResult.Exception(
                Exception("카드 등록 실패: $message"),
                "카드 등록 중 오류 발생"
            )
        }
    }

    override suspend fun deleteCard(cardId: String): ResponseResult<Unit> {
        val response = creditCardService.deleteCard(cardId)

        return if (response.isSuccessful) {
            ResponseResult.Success(Unit)
        } else {
            val rawError = response.errorBody()?.string()
            val message = rawError ?: "알 수 없는 오류"
            ResponseResult.Exception(
                Exception("카드 삭제 실패: $message"),
                "카드 삭제 중 오류 발생"
            )
        }
    }
}