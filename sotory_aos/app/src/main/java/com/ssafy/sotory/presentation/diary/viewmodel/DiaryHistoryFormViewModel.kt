package com.ssafy.sotory.presentation.diary.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.ApiResult
import com.ssafy.sotory.data.asApiResult
import com.ssafy.sotory.data.dto.diary.DiaryPaymentUpdateRequest
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.domain.payment.PaymentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface DiaryEvent {
    data object PatchDiary : DiaryEvent
}


//data class DiaryFormUiState(
//    val diary: TextFieldValue = TextFieldValue(),
//    val isValid: Boolean = false,
//    val apiState: ApiState = ApiState.Idle,
//)

sealed interface DiaryFormUiState {
    data object Idle : DiaryFormUiState
    data object Submitting : DiaryFormUiState
    data object SubmissionSuccess : DiaryFormUiState
    data class SubmissionError(val message: String) : DiaryFormUiState
}


@HiltViewModel(assistedFactory = DiaryHistoryFormViewModel.DiaryHistoryViewModelFactory::class)
class DiaryHistoryFormViewModel @AssistedInject constructor(
    @Assisted val paymentDiaryId: String,
    private val diaryRepository: DiaryRepository,
    private val paymentRepository: PaymentRepository,
) : LoggingViewModel() {
    // 단일 UI 상태를 관리
    private val _uiState = MutableStateFlow<DiaryFormUiState>(DiaryFormUiState.Idle)
    val uiState: StateFlow<DiaryFormUiState> = _uiState.asStateFlow()

    private val _diary = MutableStateFlow(TextFieldValue())
    val diary = _diary.asStateFlow()

    private val _isDiaryValid = diary.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), TextFieldValue()
    ).combine(diary) { _, current ->
        current.text.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateState(state: DiaryFormUiState) {
        _uiState.value = state
    }

    @AssistedFactory
    interface DiaryHistoryViewModelFactory {
        fun create(paymentId: String): DiaryHistoryFormViewModel
    }

    fun onEvent(event: DiaryEvent) {
        when (event) {
            is DiaryEvent.PatchDiary -> {
                patchDiary({})
            }
        }
    }

//    // 초기 검증 상태 설정
//    init {
//        viewModelScope.launch {
//            _uiState.map { it.diary.text.isNotEmpty() }.collect { isValid ->
//                _uiState.update { it.copy(isValid = isValid) }
//            }
//        }
//    }

    fun patchDiary(successCallBack: () -> Unit) {
        viewModelScope.launch {
            Log.d("DiaryHistoryFormViewModel", "patchDiary: ${_diary.value.text}")
            diaryRepository.patchDiary(
                paymentDiaryId, DiaryPaymentUpdateRequest(_diary.value.text)
            ).asApiResult().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        Log.d(
                            "DiaryHistoryFormViewModel", "ApiResult.Success: ${_diary.value.text}"
                        )
                        paymentRepository.patchPaymentDiary(
                            paymentDiaryId, DiaryPaymentUpdateRequest(_diary.value.text)
                        )
                        _uiState.update { DiaryFormUiState.SubmissionSuccess }
                        successCallBack()
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            DiaryFormUiState.SubmissionError(
                                result.exception.message ?: "알 수 없는 오류"
                            )
                        }
                    }

                    is ApiResult.Loading -> {
                        _uiState.update { DiaryFormUiState.Submitting }
                    }
                }
            }
        }
    }

    fun updateDiary(newDiary: TextFieldValue) {
        Timber.d("update Diary")
        _diary.update { newDiary }
    }

//    // 오류 상태 초기화 (에러 메시지 표시 후 닫기 등의 상황에서 호출)
//    fun resetSubmissionState() {
//        _uiState.update { it.copy(apiState = ApiState.Idle) }
//    }


    val isFormValid = combine(
        _isDiaryValid, uiState
    ) { diaryValid, uiState ->
        when (uiState) {
            is DiaryFormUiState.Idle -> diaryValid
            is DiaryFormUiState.Submitting -> false
            is DiaryFormUiState.SubmissionSuccess -> false
            is DiaryFormUiState.SubmissionError -> false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}

