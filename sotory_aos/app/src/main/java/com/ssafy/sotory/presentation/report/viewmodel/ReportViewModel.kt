package com.ssafy.sotory.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.myroom.BudgetUpdateRequest
import com.ssafy.sotory.data.dto.myroom.ReportRequest
import com.ssafy.sotory.domain.myroom.DailySpending
import com.ssafy.sotory.domain.myroom.FrequentMerchants
import com.ssafy.sotory.domain.myroom.MyRoomModel
import com.ssafy.sotory.domain.myroom.MyRoomRepository
import com.ssafy.sotory.domain.myroom.SpendingCategory
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

// 화면에 이벤트 처리
sealed interface ReportEvent {
    data object ClickSettingsButton: ReportEvent
    data object OpenSettingBudgetBottomModalSheet : ReportEvent
    data class UpdateBudget(val newBudget: Int) : ReportEvent
    data object CloseSettingBudgetBottomModalSheet : ReportEvent
    data object ClickPreviousMonth: ReportEvent
    data object ClickNextMonth: ReportEvent
}

sealed interface ReportNav {
    data object ToBack : ReportNav  // 뒤로 가기
    data object ToSetting : ReportNav  // 설정 창 가기
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val myRoomRepository: MyRoomRepository
) : ViewModel() {

    private val _reportNav = MutableSharedFlow<ReportNav>()
    val reportNav = _reportNav.asSharedFlow()

    private val _reportContent = MutableStateFlow<MyRoomModel?>(null)
    val reportContent = _reportContent.asStateFlow()

    private val _userNickname = MutableStateFlow("회원")
    val userNickname = _userNickname.asStateFlow()

    private val _currentYearMonth = MutableStateFlow(YearMonth.now(ZoneId.of("Asia/Seoul")))
    val currentYearMonth = _currentYearMonth.asStateFlow()

    private val _isNowMonth = MutableStateFlow(true)
    val isNowMonth = _isNowMonth.asStateFlow()

    private val _isSettingBudgetBottomModalSheetOpen = MutableStateFlow(false)
    val isSettingBudgetBottomModalSheetOpen = _isSettingBudgetBottomModalSheetOpen.asStateFlow()

    private val _budget = MutableStateFlow<Int?>(null)
    val budget = _budget.asStateFlow()

    private val _remainingBudget = MutableStateFlow(0)
    val remainingBudget = _remainingBudget.asStateFlow()

    private val _recommendedDailySpend = MutableStateFlow(0)
    val recommendedDailySpend = _recommendedDailySpend.asStateFlow()

    private val MIN_YEAR_MONTH = YearMonth.of(2025, 1)
    private val MAX_YEAR_MONTH = YearMonth.now(ZoneId.of("Asia/Seoul"))

    private val _isMinMonth = MutableStateFlow(false)
    val isMinMonth = _isMinMonth.asStateFlow()

    private val _isMaxMonth = MutableStateFlow(false)
    val isMaxMonth = _isMaxMonth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        calculateRecommendedDailySpend()
        updateMonthBoundaries()
        fetchReport(_currentYearMonth.value.year, _currentYearMonth.value.monthValue)
    }

    fun onEvent(event: ReportEvent) {
        when (event) {
            is ReportEvent.OpenSettingBudgetBottomModalSheet -> {
                viewModelScope.launch {
                    _isSettingBudgetBottomModalSheetOpen.value = true
                }
            }

            is ReportEvent.UpdateBudget -> {
                viewModelScope.launch {
                    _budget.value = event.newBudget
//                    _remainingBudget.value = event.newBudget - (reportContent.value?.totalSpent ?: 0)
                    calculateRecommendedDailySpend()
                    updateBudget(event.newBudget)
//                    val yearMonth = _currentYearMonth.value
//                    fetchReport(yearMonth.year, yearMonth.monthValue)
                }
            }

            is ReportEvent.CloseSettingBudgetBottomModalSheet -> {
                viewModelScope.launch {
                    _isSettingBudgetBottomModalSheetOpen.value = false
                }
            }

            is ReportEvent.ClickSettingsButton -> {
                viewModelScope.launch {
                    _reportNav.emit(ReportNav.ToSetting)
                }
            }

            is ReportEvent.ClickPreviousMonth -> {
                viewModelScope.launch {
                    if (_currentYearMonth.value > MIN_YEAR_MONTH) {
                        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
                        updateMonthBoundaries()
                        fetchReport(_currentYearMonth.value.year, _currentYearMonth.value.monthValue)
                    }
                }
            }

            is ReportEvent.ClickNextMonth -> {
                viewModelScope.launch {
                    if (_currentYearMonth.value < MAX_YEAR_MONTH) {
                        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
                        updateMonthBoundaries()
                        fetchReport(_currentYearMonth.value.year, _currentYearMonth.value.monthValue)
                    }
                }
            }
        }
    }

    suspend fun updateBudget(budget: Int) {
        val yearMonth = _currentYearMonth.value
        val request = BudgetUpdateRequest(
            month = yearMonth.toString(),
            budget = budget
        )

        when (val result = myRoomRepository.patchBudget(request)) {
            is ResponseResult.Success -> {
                _budget.value = budget
                fetchReport(yearMonth.year, yearMonth.monthValue)
                _isSettingBudgetBottomModalSheetOpen.value = false
            }

            is ResponseResult.ServerError -> {
                Log.e("ReportViewModel", "예산 수정 실패 - code: ${result.code}, msg: ${result.message}")
            }

            is ResponseResult.Exception -> {
                Log.e("ReportViewModel", "예산 수정 중 예외 발생 - ${result.message}", result.e)
            }
        }
    }

    fun fetchReport(year: Int, month: Int) {
        viewModelScope.launch {
            val result = myRoomRepository.getReport(
                year, month
            )

            when (result) {
                is ResponseResult.Success -> {
                    if (result.data.nickname != "") {
                        // 닉네임 따로 저장
                        _userNickname.value = result.data.nickname

                        // 나머지 내용 저장
                        _reportContent.value = result.data
                        _budget.value = result.data.budgetAmount
                        _remainingBudget.value = result.data.remainingBudget ?: 0
//                        _reportContent.value = dummyData
//                        _budget.value = dummyData.budgetAmount
//                        _remainingBudget.value = dummyData.remainingBudget ?: 0
                        calculateRecommendedDailySpend()
                    } else {
                        // 데이터 없으면 모든 값 초기화, nickname만 유지
                        _reportContent.value = MyRoomModel(
                            nickname = _userNickname.value, // 유지
                            budgetAmount = -1,
                            totalSpent = -1,
                            remainingBudget = -1,
                            monthlyLimit = -1,
                            budgetPercentage = -1.0,
                            categoryBreakdown = emptyList(),
                            frequentMerchants = emptyList(),
                            dailySpending = emptyList(),
                            keywordCloudUrl = ""
                        )
                        _budget.value = 0
                        _remainingBudget.value = 0
                        _recommendedDailySpend.value = 0
                    }
                }

                is ResponseResult.ServerError -> {
                    Log.e("fetchReport", "fetchReport 서버 오류 - code: ${result.code}, msg: ${result.message}")
//                    _userNickname.value = dummyData.nickname
//
//                    _reportContent.value = dummyData
//                    _budget.value = dummyData.budgetAmount
//                    _remainingBudget.value = dummyData.remainingBudget ?: 0
                }

                is ResponseResult.Exception -> {
                    Log.e("fetchReport", "fetchReport 예외 발생 - ${result.message}", result.e)
//                    _userNickname.value = dummyData.nickname
//
//                    _reportContent.value = dummyData
//                    _budget.value = dummyData.budgetAmount
//                    _remainingBudget.value = dummyData.remainingBudget ?: 0
                }
            }
        }
    }

    private fun calculateRecommendedDailySpend() {
        val today = LocalDate.now(ZoneId.of("Asia/Seoul")) // 명시적으로 한국 시간 기준
        val daysInMonth = YearMonth.of(today.year, today.monthValue).lengthOfMonth()
        val remainingDays = daysInMonth - today.dayOfMonth + 1  // 오늘 포함

        val calculatedAmount = if (_remainingBudget.value > 0 && remainingDays > 0) {
            (_remainingBudget.value / remainingDays.toFloat()).roundToInt()
        } else {
            0
        }

        _recommendedDailySpend.value = calculatedAmount
    }

    private fun updateMonthBoundaries() {
        val current = _currentYearMonth.value
        val now = YearMonth.now(ZoneId.of("Asia/Seoul"))
        Log.d("연월", "연월 : $now")

        _isMinMonth.value = current <= MIN_YEAR_MONTH
        _isMaxMonth.value = current >= MAX_YEAR_MONTH
        _isNowMonth.value = current == now
    }
}

//val dummyData = MyRoomModel(
//    nickname = "박성문",
//    budgetAmount = 1000000,
//    totalSpent = 300000,
//    remainingBudget = 0,
//    monthlyLimit = 10000000,
//    budgetPercentage = 0.3,
//    categoryBreakdown = listOf(
////            SpendingCategory("식품", 200000, 0.6),
////            SpendingCategory("생활", 100000, 0.4)
//        SpendingCategory(categoryName = "카페/음료", amount = 500000, percentage = 0.5),
//        SpendingCategory(categoryName = "식비", amount = 400000, percentage = 0.4),
//        SpendingCategory(categoryName = "생활", amount = 100000, percentage = 0.1),
//    ),
//    frequentMerchants = listOf(
//        FrequentMerchants(1, "다이소", "생활", 10, 100000),
//        FrequentMerchants(2, "메가커피", "카페/음료", 7, 63000),
//        FrequentMerchants(3, "바나프레소", "카페/음료", 6, 4000)
//    ),
////        cardDistribution = listOf(
////            SpendingCategory(categoryName = "카페/음료", amount = 500000, percentage = 0.5),
////            SpendingCategory(categoryName = "식비", amount = 400000, percentage = 0.4),
////            SpendingCategory(categoryName = "생활", amount = 100000, percentage = 0.1),
////        ),
//    dailySpending = listOf(
//        DailySpending("2025-03-01", 17000),
//        DailySpending("2025-03-02", 0),
//        DailySpending("2025-03-03", 0),
//        DailySpending("2025-03-04", 2400),
//        DailySpending("2025-03-05", 10400),
//        DailySpending("2025-03-06", 5400),
//        DailySpending("2025-03-07", 7900),
//        DailySpending("2025-03-08", 0),
//        DailySpending("2025-03-09", 11000),
//        DailySpending("2025-03-10", 37273),
//        DailySpending("2025-03-11", 38579),
//        DailySpending("2025-03-12", 13400),
//        DailySpending("2025-03-13", 9900),
//        DailySpending("2025-03-14", 9900),
//        DailySpending("2025-03-15", 0),
//        DailySpending("2025-03-16", 10000),
//        DailySpending("2025-03-17", 20290),
//        DailySpending("2025-03-18", 5900),
//        DailySpending("2025-03-19", 4900),
//        DailySpending("2025-03-20", 27400),
//        DailySpending("2025-03-21", 11600),
//        DailySpending("2025-03-22", 0),
//        DailySpending("2025-03-23", 0),
//        DailySpending("2025-03-24", 2900),
//    ),
//    keywordCloudUrl = "https://picsum.photos/200"
//)