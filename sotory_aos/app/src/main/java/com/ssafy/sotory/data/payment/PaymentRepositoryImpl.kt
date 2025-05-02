package com.ssafy.sotory.data.payment

import android.util.Log
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.data.dto.mapper.toDomain
import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest
import com.ssafy.sotory.data.dto.payment.toModel
import com.ssafy.sotory.domain.payment.PaymentCreateModel
import com.ssafy.sotory.domain.payment.PaymentListModel
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.domain.payment.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class PaymentRepositoryImpl @Inject constructor(
//    private val paymentDataSource: PaymentDataSource,
    private val paymentService: PaymentService,
) : PaymentRepository {
    private val _datePayments = MutableStateFlow<List<PaymentModel>>(emptyList())
    override val datePayments = _datePayments.asStateFlow()

    private val _dailyPayments = MutableStateFlow<List<PaymentModel>>(emptyList())
    override val dailyPayments = _dailyPayments.asStateFlow()

    override fun patchPaymentDiary(paymentDiaryId: String, request: DiaryPaymentUpdateRequest) {
        Log.d("PaymentRepositoryImpl", "patchPaymentDiary: $request")
        _dailyPayments.update {
            it.map { payment ->
                if (payment.paymentDiaryId == paymentDiaryId) {
                    Log.d("PaymentRepositoryImpl", "refresh diary: ${request.diary}")
                    payment.copy(diary = request.diary)
                } else {
                    payment
                }
            }
        }
    }

    override fun deleteDiary(paymentDiaryId: String): Flow<Unit> {
        return flow {
            _dailyPayments.update {
                it.map { payment ->
                    if (payment.paymentDiaryId == paymentDiaryId) {
                        payment.copy(diary = null)
                    } else {
                        payment
                    }
                }
            }
            Log.d(
                "PaymentRepositoryImpl",
                "_dailyPayments: [${_dailyPayments.value.size}] ${_dailyPayments.value}"
            )
        }
    }

    override fun postPayment(request: PaymentsCreateRequest): Flow<PaymentCreateModel> {
        Log.d("PaymentRepositoryImpl", "postPayment: $request")
        return flow {
            val response = paymentService.postPayment(request)
            Log.d("PaymentRepositoryImpl", "response = $response")
            val paymentCreateModel: PaymentCreateModel =
                response.data?.toDomain() ?: throw Exception(EXCEPTION_NETWORK_ERROR_MESSAGE)

            val newModel = request.toModel(
                paymentDiaryId = paymentCreateModel.paymentsDiaryId,
                paymentId = paymentCreateModel.paymentId
            )
            // 성공 시 추가
            _dailyPayments.update {
                val newList = it + newModel
                Log.d(
                    "PaymentRepositoryImpl",
                    "기존 리스트 [${it.size}], 추가된 리스트[${newList.size}]: $newList"
                ) // 이 안에서 찍어줘야 정확
                newList
            }

            Log.d(
                "PaymentRepositoryImpl",
                "_datePayments [${_dailyPayments.value.size}]: ${_dailyPayments.value}"
            )
            emit(paymentCreateModel)
        }.flowOn(Dispatchers.IO)
    }

    override fun patchPayment(
        paymentId: String,
        request: PaymentsUpdateRequest,
    ): Flow<Unit> {
        return flow {
            val response = paymentService.patchPayment(paymentId, request)

//            val transactionDate = request.paidAt.split("T").getOrNull(0) ?: ""
//            val transactionTime = request.paidAt.split("T").getOrNull(1)?.substringBefore(".") ?: ""

            _dailyPayments.update { payments ->
                payments.map { payment ->
                    if (payment.paymentId == paymentId) {
                        // Update the payment with the new values from the request
                        payment.copy(
                            categoryName = request.categoryName ?: payment.categoryName,
                            merchantName = request.merchantName ?: payment.merchantName,
                            transactionBalance = request.transactionBalance
                                ?: payment.transactionBalance,
                            transactionTime = request.transactionTime ?: payment.transactionTime,
                        )
                    } else {
                        payment
                    }
                }
            }
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun deletePayment(paymentId: String): Flow<Unit> {
        return flow {
            val response = paymentService.deletePayment(paymentId)

            _dailyPayments.update {
                it.filter { payment ->
                    payment.paymentId != paymentId
                }
            }

            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchDatePayments(date: String): Flow<List<PaymentModel>> {
        return flow {
            val response = paymentService.getDatePayments(date).data
            val payments = response?.paymentDiaires?.map { it.toDomain() } ?: emptyList()
            _datePayments.update {
                payments
            }
            Log.d(
                "PaymentRepositoryImpl",
                "fetchDatePayments [${_datePayments.value.size}]: ${_datePayments.value}"
            )
            emit(payments)
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchDailyPayments(): Flow<PaymentListModel> {
        return flow {
            val response = paymentService.getDailyPayments().data
            val payments = response?.paymentDiaires?.map { it.toDomain() } ?: emptyList()
            _dailyPayments.update {
                payments
            }
            val paymentListModel = PaymentListModel(
                paymentDiaires = payments, isCardCreated = response?.isCardCreated ?: false
            )
            Log.d(
                "PaymentRepositoryImpl",
                "fetchDatePayments [${_dailyPayments.value.size}]: ${_dailyPayments.value}"
            )
            emit(paymentListModel)
        }.flowOn(Dispatchers.IO)
    }

    // 캐시된 데이터를 관찰하기 위한 메서드 추가
    override fun observeDailyPayments(): Flow<List<PaymentModel>> {
        return dailyPayments
    }

    override fun observeDatePayments(): Flow<List<PaymentModel>> {
        return datePayments
    }


    companion object {
        private const val EXCEPTION_NETWORK_ERROR_MESSAGE =
            "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
    }
}