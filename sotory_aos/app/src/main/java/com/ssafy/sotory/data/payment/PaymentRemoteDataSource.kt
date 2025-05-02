//package com.ssafy.sotory.data.payment
//
//import com.ssafy.sotory.data.ApiResponseHandler.handleApiResponse
//import com.ssafy.sotory.data.ResponseResult
//import com.ssafy.sotory.data.dto.payment.DairyPaymentsListResponse
//import com.ssafy.sotory.data.dto.payment.PaymentsCreateRequest
//import com.ssafy.sotory.data.dto.payment.PaymentsCreateResponse
//import com.ssafy.sotory.data.dto.payment.PaymentsUpdateRequest
//import javax.inject.Inject
//
//class PaymentRemoteDataSource @Inject constructor(private val paymentService: PaymentService) :
//    PaymentDataSource {
//    override suspend fun postPayment(request: PaymentsCreateRequest): ResponseResult<PaymentsCreateResponse> =
//        handleApiResponse {
//            paymentService.postPayment(request)
//        }
//
//
//    override suspend fun patchPayment(
//        paymentsId: String,
//        request: PaymentsUpdateRequest,
//    ): ResponseResult<Unit> = handleApiResponse {
//        paymentService.patchPayment(paymentsId, request)
//    }
//
//    override suspend fun deletePayment(paymentsId: String): ResponseResult<Unit> =
//        handleApiResponse {
//            paymentService.deletePayment(paymentsId)
//        }
//
//    override suspend fun getDatePayments(date: String): ResponseResult<List<DairyPaymentsListResponse>> =
//        handleApiResponse {
//            paymentService.getDatePayments(date)
//        }
//
//    override suspend fun getDailyPayments(): ResponseResult<List<DairyPaymentsListResponse>> =
//        handleApiResponse {
//            paymentService.getDailyPayments()
//        }
//}