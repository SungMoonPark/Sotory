package com.ssafy.sotory.presentation.settings.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.userinfo.UserInfoUpdateNicknameRequest
import com.ssafy.sotory.domain.settings.SettingsRepository
import com.ssafy.sotory.domain.userinfo.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 화면에 이벤트 처리
sealed interface SettingsProfileEvent {
    data object ClickToBack: SettingsProfileEvent  // 뒤로 가기 버튼 클릭
    data object ClickAllDelete: SettingsProfileEvent  // 글자 전체 삭제
    data object ClickEditNickname: SettingsProfileEvent  // 닉네임 수정
}

// 이벤트에서 화면 이동
sealed interface SettingsProfileNav {
    data object ToBack : SettingsProfileNav  // 뒤로 가기
}

@HiltViewModel
data class SettingsProfileViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository
): LoggingViewModel() {

    private val _settingsProfileNav = MutableSharedFlow<SettingsProfileNav>()
    val settingsProfileNav = _settingsProfileNav.asSharedFlow()

    private val _userName = MutableStateFlow(TextFieldValue())
    val userName = _userName.asStateFlow()

    fun updateUserName(newName: TextFieldValue) {
        _userName.value = newName
    }

    private fun deleteAllUserName(){
        _userName.value = TextFieldValue()
    }

    fun updateNicknameToServer() {
        viewModelScope.launch {
            val request = UserInfoUpdateNicknameRequest(nickname = userName.value.text)
            when (val result = userInfoRepository.patchNickname(request)) {
                is ResponseResult.Success -> {
                    Log.d("patchNickname", "닉네임 변경 성공")
                    userInfoRepository.getUserInfo()
                    _settingsProfileNav.emit(SettingsProfileNav.ToBack)
                }
                is ResponseResult.ServerError -> {
                    Log.e("patchNickname", "서버 오류: ${result.code} - ${result.message}")
                }
                is ResponseResult.Exception -> {
                    Log.e("patchNickname", "예외 발생: ${result.message}", result.e)
                }
            }
        }
    }

    fun onEvent(event: SettingsProfileEvent) {
        when (event) {
            is SettingsProfileEvent.ClickToBack -> {
                viewModelScope.launch {
                    _settingsProfileNav.emit(SettingsProfileNav.ToBack)
                }
            }

            is SettingsProfileEvent.ClickAllDelete -> {
                viewModelScope.launch {
                    deleteAllUserName()
                }
            }

            is SettingsProfileEvent.ClickEditNickname -> {
                viewModelScope.launch {
                    updateNicknameToServer()
                }
            }

        }
    }
}
