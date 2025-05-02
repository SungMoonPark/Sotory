package com.ssafy.sotory.presentation.auth.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 화면에 이벤트 처리
sealed interface TermsEvent {
    data class CheckAllTerms(val id: String): TermsEvent  // 전체 동의
    data class CheckSelectedTerms(val index: Int) : TermsEvent  // 단일 항목 동의
    data class ClickAgreement(val id: String) : TermsEvent  // 인증 하기 버튼
}

// 이벤트에서 화면 이동
sealed interface TermsNav {
    data object ToBack : TermsNav
    data object ToNext : TermsNav
}

@HiltViewModel
class TermsViewModel @Inject constructor(
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<TermsNav>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _isCheckedAll = MutableStateFlow(false)
    val isCheckedAll = _isCheckedAll.asStateFlow()

    private val _isCheckedTerms = MutableStateFlow(listOf(false, false, false, false))
    val isCheckedTerms = _isCheckedTerms.asStateFlow()

    fun onEvent(event: TermsEvent) {
        when (event) {
            is TermsEvent.CheckAllTerms -> {
                val newState = !_isCheckedAll.value
                _isCheckedAll.value = newState
                _isCheckedTerms.value = List(4) { newState }
            }

            is TermsEvent.CheckSelectedTerms -> {
                val updatedList = _isCheckedTerms.value.toMutableList()
                updatedList[event.index] = !updatedList[event.index]
                _isCheckedTerms.value = updatedList
                _isCheckedAll.value = updatedList.all { it }
            }

            is TermsEvent.ClickAgreement -> {
                if (_isCheckedTerms.value[0] && _isCheckedTerms.value[1]) {
                    viewModelScope.launch {
                        _navigationEvent.emit(TermsNav.ToNext)
                    }
                }
            }

        }
    }
}