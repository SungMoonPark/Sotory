package com.ssafy.sotory.common

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.data.TokenManager
import com.ssafy.sotory.data.datastore.ProfileDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

enum class SplashRouteType {
    HOME, SELECTAPP, PROFILE, PERMISSION, KIOSK
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager, // 로그인 상태 확인용
    private val profileDataStore: ProfileDataStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _splashRouteType = mutableStateOf(SplashRouteType.SELECTAPP)
    val splashRouteType: State<SplashRouteType> = _splashRouteType

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            runBlocking {
                val isFirstJoin = profileDataStore.isFirstJoinFlow.map { isJoinFirst ->
                    isJoinFirst.takeIf {
                        it != null
                    }
                }.first()

                if (isFirstJoin == null) {
                    _splashRouteType.value = SplashRouteType.PERMISSION
                }

                _isLoading.value = false
            }
        }
    }
}
