package com.ssafy.sotory.presentation.diary.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.domain.diary.OtherDiaryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OtherDiaryUiState {
    data class Success(val diaries: OtherDiaryModel) : OtherDiaryUiState
    data class Error(val message: String = "") : OtherDiaryUiState
    data object Loading : OtherDiaryUiState
}

@HiltViewModel
class OtherDiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : LoggingViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val otherDiaryUiState = combine(
        diaryRepository.observeOtherDiaries(), _isLoading, _error
    ) { diaries, isLoading, error ->
        when {
            isLoading -> OtherDiaryUiState.Loading
            error != null -> OtherDiaryUiState.Error(error)
            else -> OtherDiaryUiState.Success(diaries)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OtherDiaryUiState.Loading,
    )


    init {
        refreshOtherDiaries()
    }

    private fun refreshOtherDiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                diaryRepository.fetchOtherDiaries().collect()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


}