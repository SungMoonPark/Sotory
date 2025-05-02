package com.ssafy.sotory.data.payment

import com.ssafy.sotory.data.BaseResponse
import com.ssafy.sotory.data.dto.payment.DairyPaymentsListResponse
import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsCreateResponse
import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class DiaryWrap(
    val paymentDiaires: List<DairyPaymentsListResponse>,
    val isCardCreated: Boolean?,
)

interface PaymentService {
    /// 소비 내역 추가
    @POST(PAYMENT_PATH)
    suspend fun postPayment(
        @Body request: PaymentsCreateRequest,
    ): BaseResponse<PaymentsCreateResponse>

    /// 소비 내역 수정
    @PATCH(PAYMENT_MODIFY_PATH)
    suspend fun patchPayment(
        @Path("paymentsId") paymentsId: String,
        @Body request: PaymentsUpdateRequest,
    ): BaseResponse<Unit>

    /// 소비 내역 삭제
    @DELETE(PAYMENT_MODIFY_PATH)
    suspend fun deletePayment(
        @Path("paymentsId") paymentsId: String,
    ): BaseResponse<Unit>

    /// 일별 소비 내역 및 일기 조회
    @GET(PAYMENT_PATH)
    suspend fun getDatePayments(
        @Query("date") date: String,
    ): BaseResponse<DiaryWrap>

    /// 금일 소비 내역 및 일기 조회 - (Redis)
    @GET(PAYMENT_DAILY_PATH)
    suspend fun getDailyPayments(
    ): BaseResponse<DiaryWrap>


    companion object {
        private const val PAYMENT_DAILY_PATH = "/diaries/payments/today"
        private const val PAYMENT_PATH = "/diaries/payments"
        private const val PAYMENT_MODIFY_PATH = "/diaries/payments/{paymentsId}"
    }
}