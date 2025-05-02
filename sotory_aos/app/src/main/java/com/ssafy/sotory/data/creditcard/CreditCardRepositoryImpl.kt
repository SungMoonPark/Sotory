package com.ssafy.sotory.data.creditcard

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.domain.creditcard.OwnedCardListModel
import com.ssafy.sotory.domain.creditcard.ConnectedCardListModel
import com.ssafy.sotory.domain.creditcard.CreditCardRepository
import com.ssafy.sotory.domain.creditcard.OwnedCardListWrapperModel
import javax.inject.Inject

data class CreditCardRepositoryImpl @Inject constructor(
    private val creditCardDataSource: CreditCardDataSource
) : CreditCardRepository {
    override suspend fun getMyOwnedCard(): ResponseResult<OwnedCardListWrapperModel> {
        return when (val result = creditCardDataSource.getMyOwnedCard()) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.toDomain())
        }
    }

    override suspend fun getMyConnectedCard(): ResponseResult<List<ConnectedCardListModel>> {
        return when (val result = creditCardDataSource.getMyConnectedCard()) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.map { it.toDomain() })
        }
    }

    override suspend fun postCard(request: List<CardRegistrationRequest>): ResponseResult<Unit> {
        return when (val result = creditCardDataSource.postCard(request)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    override suspend fun deleteCard(cardId: String): ResponseResult<Unit> {
        return when (val result = creditCardDataSource.deleteCard(cardId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(
                result.e, EXCEPTION_NETWORK_ERROR_MESSAGE
            )

            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    companion object {
        private const val EXCEPTION_NETWORK_ERROR_MESSAGE =
            "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
    }

}
