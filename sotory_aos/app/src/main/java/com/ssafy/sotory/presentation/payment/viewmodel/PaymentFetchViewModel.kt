package com.ssafy.sotory.presentation.payment.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.domain.payment.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface PaymentReadUiState {
    data class Success(val payments: List<PaymentModel>) : PaymentReadUiState
    data class Error(val message: String) : PaymentReadUiState
    data object Loading : PaymentReadUiState
}

@HiltViewModel
class PaymentFetchViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val savedStateHandle: SavedStateHandle,
) : LoggingViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val paymentReadUiState: StateFlow<PaymentReadUiState> = combine(
        paymentRepository.observeDatePayments(), _isLoading, _error
    ) { payments, isLoading, error ->
        when {
            isLoading -> PaymentReadUiState.Loading
            error != null -> PaymentReadUiState.Error(error)
            else -> PaymentReadUiState.Success(payments)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaymentReadUiState.Loading,
    )

    init {
        val date = savedStateHandle.get<String>("date") ?: LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        refreshDatePayments(date)
    }

    private fun refreshDatePayments(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                paymentRepository.fetchDatePayments(date).collect{
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

}