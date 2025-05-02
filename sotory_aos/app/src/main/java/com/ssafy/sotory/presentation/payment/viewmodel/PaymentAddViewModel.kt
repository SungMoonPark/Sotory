package com.ssafy.sotory.presentation.payment.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.ApiResult
import com.ssafy.sotory.data.asApiResult
import com.ssafy.sotory.data.dto.payment.Category
import com.ssafy.sotory.domain.payment.PaymentRepository
import com.ssafy.sotory.domain.payment.PaymentUIState
import com.ssafy.sotory.domain.payment.toCreateRequest
import com.ssafy.sotory.domain.payment.toPatchRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface PaymentFormUiState {
    data object Idle : PaymentFormUiState
    data object Submitting : PaymentFormUiState
    data class SubmissionSuccess(val paymentId: String) : PaymentFormUiState
    data class SubmissionError(val message: String) : PaymentFormUiState
}


@HiltViewModel
class PaymentAddViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
) : LoggingViewModel() {
    private val _formUiState = MutableStateFlow<PaymentFormUiState>(PaymentFormUiState.Idle)
    val formUiState = _formUiState.asStateFlow()


    fun createPayment() {
        if (!isFormValid.value) return

        viewModelScope.launch {
            val prevRequest = toPaymentUIState().toCreateRequest()

//            val formatRequest = prevRequest.copy(
//                transactionTime = formatTimeWithCurrentDate(prevRequest.transactionTime)
//            )
//            Log.d("PaymentAddViewModel", "createPayment: $formatRequest")
            paymentRepository.postPayment(request = prevRequest).asApiResult().collect { result ->
                _formUiState.value = when (result) {
                    is ApiResult.Success -> PaymentFormUiState.SubmissionSuccess(result.data.paymentId)
                    is ApiResult.Error -> PaymentFormUiState.SubmissionError(
                        result.exception.message ?: "알 수 없는 오류"
                    )

                    is ApiResult.Loading -> PaymentFormUiState.Submitting
                }
            }
        }
    }

    fun updatePayment(paymentId: String) {
        Log.d("PaymentAddViewModel", "updatePayment: ${isFormValid.value}")
        if (!isFormValid.value) return


        viewModelScope.launch {
            val request = toPaymentUIState().toPatchRequest()
            paymentRepository.patchPayment(paymentId = paymentId, request = request).asApiResult()
                .collect { result ->
                    _formUiState.value = when (result) {
                        is ApiResult.Success -> PaymentFormUiState.SubmissionSuccess(paymentId)
                        is ApiResult.Error -> PaymentFormUiState.SubmissionError(
                            result.exception.message ?: "알 수 없는 오류"
                        )

                        is ApiResult.Loading -> PaymentFormUiState.Submitting
                    }
                }
        }


    }

    // 개별 필드 상태
    private val _merchantName = MutableStateFlow(TextFieldValue())
    val merchantName = _merchantName.asStateFlow()

    private val _transactionBalance = MutableStateFlow(TextFieldValue())
    val transactionBalance = _transactionBalance.asStateFlow()


    private val _transactionTime = MutableStateFlow(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )
    val transactionTime = _transactionTime.asStateFlow()

    private val _categoryName = MutableStateFlow<Category?>(null)
    val categoryName = _categoryName.asStateFlow()

    // 개별 필드 업데이트 함수
    fun updateMerchantName(merchantName: TextFieldValue) {
        Log.d("PaymentViewModel", "updateMerchantName: $merchantName")
        _merchantName.value = merchantName
    }

    fun updateTransactionBalance(transactionBalance: TextFieldValue) {
        Log.d("PaymentViewModel", "updateTransactionBalance: $transactionBalance")
        _transactionBalance.value = transactionBalance
    }


    fun updateTransactionTime(transactionTime: String) {
        Log.d("PaymentViewModel", "updateTransactionTime: $transactionTime")
        _transactionTime.value = transactionTime
    }

    fun updateCategoryName(categoryName: Category) {
        Log.d("PaymentViewModel", "updateCategoryName: $categoryName")
        _categoryName.value = categoryName
    }

    // 개별 필드별 유효성 검사 결과
    val isMerchantNameValid = merchantName.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), TextFieldValue()
    ).combine(merchantName) { _, current ->
        current.text.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isTransactionBalanceValid = transactionBalance.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), TextFieldValue()
    ).combine(transactionBalance) { _, current ->
        current.text.isNotEmpty() && current.text.toDoubleOrNull() != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isTransactionTimeValid = transactionTime.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), TextFieldValue()
    ).combine(transactionTime) { _, current ->
        current.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isCategoryNameValid = categoryName.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), TextFieldValue()
    ).combine(categoryName) { _, current ->
        current != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 전체 폼의 유효성 검사 결과 (모든 필드가 유효할 때만 true)
    val isFormValid = combine(
        isMerchantNameValid, isTransactionBalanceValid, isTransactionTimeValid, isCategoryNameValid
    ) { merchantNameValid, balanceValid, timeValid, categoryValid ->
        merchantNameValid && balanceValid && timeValid && categoryValid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 현재 폼 데이터를 기반으로 PaymentUIState 객체 생성
    private fun toPaymentUIState() = PaymentUIState(
        categoryName = categoryName.value!!.categoryName,
        merchantName = merchantName.value.text,
        transactionBalance = transactionBalance.value.text,
        transactionTime = transactionTime.value,
        enabled = isFormValid.value
    )
}