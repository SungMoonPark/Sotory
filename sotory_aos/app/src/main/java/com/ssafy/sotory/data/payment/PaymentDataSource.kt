package com.ssafy.sotory.data.payment

import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.payment.DairyPaymentsListResponse
import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
import com.ssafy.sotory.data.dto.payment.PaymentsCreateResponse
import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest

//interface PaymentDataSource {
//    suspend fun postPayment(request: PaymentsCreateRequest): ResponseResult<PaymentsCreateResponse>
//    suspend fun patchPayment(
//        paymentsId: String,
//        request: PaymentsUpdateRequest,
//    ): ResponseResult<Unit>
//    suspend fun deletePayment(paymentsId: String): ResponseResult<Unit>
//    suspend fun getDatePayments(date: String): ResponseResult<List<DairyPaymentsListResponse>>
//    suspend fun getDailyPayments(): ResponseResult<List<DairyPaymentsListResponse>>
//}
