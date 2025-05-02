package com.ssafy.sotory.presentation.settings.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.ApiResponseHandler.onException
import com.ssafy.sotory.data.ApiResponseHandler.onServerError
import com.ssafy.sotory.data.ApiResponseHandler.onSuccess
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.TokenManager
import com.ssafy.sotory.data.dto.settings.AppLogOutRequest
import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.domain.settings.SettingsRepository
import com.ssafy.sotory.domain.userinfo.UserInfoModel
import com.ssafy.sotory.domain.userinfo.UserInfoRepository
import com.ssafy.sotory.presentation.payment.viewmodel.NavigationAction
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 화면에 이벤트 처리
sealed interface SettingsMainEvent {
    data object ClickToBack: SettingsMainEvent  // 뒤로 가기 버튼 클릭
    data object EditProfile : SettingsMainEvent  // 프로필 수정 버튼 클릭
    data object ClickToListCard : SettingsMainEvent  // 내 카드 목록 버튼 클릭
    data object ClickToSetAlarm : SettingsMainEvent  // 알람 설정 버튼 클릭
    data object ClickToReadTerms : SettingsMainEvent  // 약관 버튼 클릭
    data object ClickToLogOut : SettingsMainEvent  // 로그아웃 버튼 클릭
    data object ClickToLogOutDismiss : SettingsMainEvent  // 로그아웃 취소 버튼 클릭
    data object ClickToLogOutConfirm : SettingsMainEvent  // 로그아웃 확인 버튼 클릭
}

// 이벤트에서 화면 이동
sealed interface SettingsMainNav {
    data object ToBack : SettingsMainNav  // 뒤로 가기
    data object ToEditProfile : SettingsMainNav  // 프로필 수정
    data object ToListCard : SettingsMainNav  // 내 카드 목록 보기
    data object ToSetAlarm : SettingsMainNav  // 알람 설정
    data object ToTerms : SettingsMainNav  // 약관 화면 가기
    data object ToLogOut : SettingsMainNav  // 로그아웃 후 로그인 페이지로 이동
}

@HiltViewModel
data class SettingsMainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val userInfoRepository: UserInfoRepository,
    private val tokenManager: TokenManager
): LoggingViewModel() {

    private val _settingsMainNav = MutableSharedFlow<SettingsMainNav>()
    val settingsMainNav = _settingsMainNav.asSharedFlow()

    private val _isLogOutDialogOpen = MutableStateFlow(false)
    val isLogOutDialogOpen: StateFlow<Boolean> = _isLogOutDialogOpen.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo: StateFlow<UserInfoModel?> = _userInfo.asStateFlow()

    fun fetchUserInfo() {
        viewModelScope.launch {
            when (val result = userInfoRepository.getUserInfo()) {
                is ResponseResult.Success -> {
                    _userInfo.value = result.data
                    Log.d("getUserInfo", "getUserInfo 불러오기 성공: ${result.data}")
                }

                is ResponseResult.ServerError -> {
                    Log.e("getUserInfo", "getUserInfo 서버 오류 - code: ${result.code}, msg: ${result.message}")
                }

                is ResponseResult.Exception -> {
                    Log.e("getUserInfo", "getUserInfo 예외 발생 - ${result.message}", result.e)
                }
            }
        }
    }

    private fun postLogOut() {
        viewModelScope.launch {
            val refreshToken = tokenManager.getRefreshToken()

            refreshToken?.let {
                val result = settingsRepository.postAppLogOut(
                    AppLogOutRequest(refreshToken = it)
                )

                result.onSuccess {
                    Log.i("SettingsViewModel", "로그아웃 성공")
                    tokenManager.clearTokens()
                    _settingsMainNav.emit(SettingsMainNav.ToLogOut)
                }.onServerError { code, message ->
                    Log.e("SettingsViewModel", "서버 오류: $code $message")

                    if (code == "403") {
                        Log.w("SettingsViewModel", "403 오류 발생, 강제 로그아웃 처리")
                        tokenManager.clearTokens()
                        _settingsMainNav.emit(SettingsMainNav.ToLogOut)
                    }
                }.onException { throwable, message ->
                    Log.e("SettingsViewModel", "예외 발생: $message", throwable)
                }
            } ?: run {
                Log.e("SettingsViewModel", "예외 발생: refreshToken이 null 값")
                // refreshToken이 null이라면 이미 로그아웃 상태이므로 강제로 로그아웃 처리
                tokenManager.clearTokens()
                _settingsMainNav.emit(SettingsMainNav.ToLogOut)
            }
        }
    }

    fun onEvent(event: SettingsMainEvent) {
        when (event) {
            is SettingsMainEvent.ClickToBack -> {
                viewModelScope.launch {
                    _settingsMainNav.emit(SettingsMainNav.ToBack)
                }
            }

            is SettingsMainEvent.EditProfile -> {
                viewModelScope.launch {
                    _settingsMainNav.emit(SettingsMainNav.ToEditProfile)
                }
            }

            is SettingsMainEvent.ClickToListCard -> {
                viewModelScope.launch {
                    _settingsMainNav.emit(SettingsMainNav.ToListCard)
                }
            }

            is SettingsMainEvent.ClickToSetAlarm -> {
                viewModelScope.launch {
                    _settingsMainNav.emit(SettingsMainNav.ToSetAlarm)
                }
            }

            is SettingsMainEvent.ClickToReadTerms -> {
                viewModelScope.launch {
                    _settingsMainNav.emit(SettingsMainNav.ToTerms)
                }
            }

            is SettingsMainEvent.ClickToLogOut -> {
                viewModelScope.launch {
                    _isLogOutDialogOpen.value = true
                    val accessToken = tokenManager.getAccessToken()
                    val refreshToken = tokenManager.getRefreshToken()
                    Log.d("DEBUG", "accessToken: $accessToken")
                    Log.d("DEBUG", "refreshToken: $refreshToken")
                }
            }

            is SettingsMainEvent.ClickToLogOutDismiss -> {
                viewModelScope.launch {
                    _isLogOutDialogOpen.value = false
                }
            }

            is SettingsMainEvent.ClickToLogOutConfirm -> {
                viewModelScope.launch {
                    _isLogOutDialogOpen.value = false
                    postLogOut()
                }
            }
        }
    }

}