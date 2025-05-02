package com.ssafy.sotory.presentation.diary.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.domain.diary.DiaryMonthModel
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.presentation.diary.ScreenStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

sealed interface DiaryUiState {
    data class Success(val payments: List<DiaryMonthModel>) : DiaryUiState
    data class Error(val message: String = "") : DiaryUiState
    data object Loading : DiaryUiState
}

@HiltViewModel
class DiaryMonthViewModel @Inject constructor(private val diaryRepository: DiaryRepository) :
    LoggingViewModel() {
    private val _year = MutableStateFlow(LocalDate.now().year)
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(1)
    val month = _month.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val _screenState = MutableStateFlow(ScreenStep.MONTH_CARD)
    val screenState = _screenState.asStateFlow()
    private val _currentDay = MutableStateFlow(1)
    val currentDay = _currentDay.asStateFlow()


    fun updateScreenState(screenStep: ScreenStep) {
        _screenState.value = screenStep
    }

    fun updateCurrentDay(day: Int) {
        _currentDay.value = day
    }

    val maxDateCardCount = combine(year, month) { year, month ->
        val yearMonth = YearMonth.of(year, month)
        yearMonth.lengthOfMonth()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dateDiaries = diaryRepository.observeDateDiaries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mapOf()
    )

    val diaryMonthUiState: StateFlow<DiaryUiState> = combine(
        diaryRepository.observeMonthDiaries(), _isLoading, _error
    ) { monthDiaries, isLoading, error ->
        when {
            isLoading -> DiaryUiState.Loading
            error != null -> DiaryUiState.Error(error)
            else -> DiaryUiState.Success(monthDiaries)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiaryUiState.Loading
    )

    init {
        // 년도가 변경될 때마다 일기 데이터를 새로 가져오기
        viewModelScope.launch {
            year.collect { newYear ->
//                Log.d("DiaryMonthViewModel", "year changed: $newYear")
                refreshMonthDiaries(newYear)
            }
        }

        // 년도와 월이 변경될 때마다 날짜별 일기 데이터를 새로 가져오기
        viewModelScope.launch {
            combine(year, month) { y, m -> Pair(y, m) }.collect { (y, m) ->
                refreshDateDiaries(y, m)
            }
        }
    }

    private fun refreshMonthDiaries(year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                diaryRepository.fetchMonthDiaries(year).collect()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun refreshDateDiaries(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                diaryRepository.fetchDateDiaries(year, month).collect()
            } catch (e: Exception) {
                // 에러 처리 추가
                _error.value = e.message
            }
        }
    }


    fun updateYear(year: Int) {
        _year.value = year
    }

    fun updateMonth(month: Int) {
        _month.value = month
    }


}
