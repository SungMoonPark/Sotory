package com.ssafy.sotory.domain.payment

import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest
import kotlinx.coroutines.flow.Flow


interface PaymentRepository {
    val dailyPayments: Flow<List<PaymentModel>>
    val datePayments: Flow<List<PaymentModel>>

    /// 소비 내역 추가
    fun postPayment(request: PaymentsCreateRequest): Flow<PaymentCreateModel>

    /// 소비 내역 수정
    // todo 응답 수정
    fun patchPayment(
        paymentId: String,
        request: PaymentsUpdateRequest,
    ): Flow<Unit>

    /// 소비 내역 삭제
    // todo 응답 수정
    fun deletePayment(
        paymentId: String,
    ): Flow<Unit>

    /// 일별 소비 내역 및 일기 조회
    fun fetchDatePayments(
        date: String,
    ): Flow<List<PaymentModel>>


    /// 금일 소비 내역 및 일기 조회
    fun fetchDailyPayments(): Flow<PaymentListModel>

    fun patchPaymentDiary(paymentDiaryId: String, request: DiaryPaymentUpdateRequest)

    /// 일기 로컬 삭제
    // todo 응답 수정
    fun deleteDiary(
        paymentDiaryId: String,
    ): Flow<Unit>

    fun observeDailyPayments(): Flow<List<PaymentModel>>
    fun observeDatePayments(): Flow<List<PaymentModel>>
}