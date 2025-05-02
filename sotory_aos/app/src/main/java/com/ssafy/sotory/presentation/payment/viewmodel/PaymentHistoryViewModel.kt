package com.ssafy.sotory.presentation.payment.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.ssafy.sotory.data.ApiResult
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.asApiResult
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.domain.payment.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PaymentHistoryUiState {
    data class Success(val payments: List<PaymentModel>) : PaymentHistoryUiState
    data class Error(val message: String = "") : PaymentHistoryUiState
    data object Loading : PaymentHistoryUiState
}

// 화면에 이벤트 처리
sealed interface PaymentEvent {
    data object AddClicked : PaymentEvent
    data class EditPaymentClicked(val payment: PaymentModel) : PaymentEvent
    data class DeletePaymentClicked(val paymentId: String) : PaymentEvent
    data class AddDiary(val paymentId: String, val request: DiaryPaymentUpdateRequest) :
        PaymentEvent

    data class DeleteDiaryClicked(val paymentDiaryId: String) : PaymentEvent
    data class EditDiaryClicked(val payment: PaymentModel) : PaymentEvent
    data object RefreshPayments : PaymentEvent
}

// 이벤트에서 화면 이동
sealed interface NavigationAction {
    data object ToPaymentAdd : NavigationAction
    data class ToPaymentEdit(val payment: PaymentModel) : NavigationAction
    data class ToDiaryEdit(val payment: PaymentModel) : NavigationAction
}

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val diaryRepository: DiaryRepository,
) : LoggingViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationAction>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val _isCreated = MutableStateFlow(false)
    val isCreated = _isCreated.asStateFlow()

//    val paymentHistoriesUiState: StateFlow<PaymentHistoryUiState> = paymentHistoryUiState(
//        paymentRepository
//    ).stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = PaymentHistoryUiState.Loading,
//    )

    // StateFlow와 API 호출 결과를 결합한 UI 상태
    val paymentHistoriesUiState: StateFlow<PaymentHistoryUiState> = combine(
        paymentRepository.observeDailyPayments(), _isLoading, _error
    ) { payments, isLoading, error ->
        when {
            isLoading -> PaymentHistoryUiState.Loading
            error != null -> PaymentHistoryUiState.Error(error)
            else -> PaymentHistoryUiState.Success(payments)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaymentHistoryUiState.Loading,
    )

    val enabledAddButton: StateFlow<Boolean> = paymentHistoriesUiState.map { uiState ->
        when (uiState) {
            is PaymentHistoryUiState.Success -> {
                uiState.payments.any { it.diary != null }
            }

            is PaymentHistoryUiState.Error -> false
            is PaymentHistoryUiState.Loading -> false
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    init {
        refreshDailyPayments()
    }

    private fun refreshDailyPayments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                paymentRepository.fetchDailyPayments().collect { it ->
                    _isCreated.value = it.isCardCreated
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.AddClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationAction.ToPaymentAdd)
                }
            }

            is PaymentEvent.EditPaymentClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationAction.ToPaymentEdit(event.payment))
                }
            }

            is PaymentEvent.EditDiaryClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationAction.ToDiaryEdit(event.payment))
                }
            }

            is PaymentEvent.DeleteDiaryClicked -> {
                viewModelScope.launch {
                    try {
                        diaryRepository.deleteDiary(event.paymentDiaryId).collect()

                        // 로컬 일기 데이터 삭제
                        paymentRepository.deleteDiary(paymentDiaryId = event.paymentDiaryId)
                            .collect()
                    } catch (e: Exception) {
                        _error.value = e.message
                    }
                }
            }

            is PaymentEvent.AddDiary -> {
                viewModelScope.launch {
                    try {
                        diaryRepository.patchDiary(event.paymentId, event.request).collect()
                        paymentRepository.patchPaymentDiary(event.paymentId, event.request)
                    } catch (e: Exception) {
                        _error.value = e.message
                    }
                }
            }

            is PaymentEvent.RefreshPayments -> {
                refreshDailyPayments()
            }

            is PaymentEvent.DeletePaymentClicked -> {
                viewModelScope.launch {
                    try {
                        paymentRepository.deletePayment(event.paymentId).collect()
                    } catch (e: Exception) {
                        _error.value = e.message
                    }
                }

            }
        }
    }
}

//private fun paymentHistoryUiState(
//    paymentRepository: PaymentRepository,
//): Flow<PaymentHistoryUiState> {
//    val cacheDailyPayments = paymentRepository.observeDailyPayments()
//
//
//    return paymentRepository.fetchDailyPayments().asApiResult()
//        .combine(cacheDailyPayments) { paymentResponseResult, cache ->
//            when (paymentResponseResult) {
//                is ApiResult.Success -> PaymentHistoryUiState.Success(paymentResponseResult.data)
//                is ApiResult.Loading -> PaymentHistoryUiState.Loading
//                is ApiResult.Error -> PaymentHistoryUiState.Error(paymentResponseResult.exception.message.toString())
//            }
//
//        }
//
//
//}
