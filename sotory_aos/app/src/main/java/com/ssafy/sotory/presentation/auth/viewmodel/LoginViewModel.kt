package com.ssafy.sotory.presentation.auth.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.TokenManager
import com.ssafy.sotory.data.dto.auth.SignUpLoginRequest
import com.ssafy.sotory.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    fun kakaoLogin(
        activity: Activity,
        onSuccess: (String) -> Unit = {},
        onFailure: (Throwable?) -> Unit = {}
    ) {
        val loginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                onFailure(error)
            } else if (token != null) {
                viewModelScope.launch {
                    tokenManager.saveAccessToken(token.accessToken)
                    token.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                }
                onSuccess(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = loginCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = loginCallback)
        }
    }

    fun appLogin(
        kakaoAccessToken: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = SignUpLoginRequest(
                    accessToken = kakaoAccessToken,
                )

                when (val result = authRepository.postSignUpLogin(request)) {
                    is ResponseResult.Success -> {
                        val tokenData = result.data
                        tokenManager.saveAccessToken(tokenData.accessToken)
                        tokenManager.saveRefreshToken(tokenData.refreshToken)
                        onSuccess()
                    }

                    is ResponseResult.ServerError -> {
                        onFailure(Exception("서버 오류: ${result.message}"))
                    }

                    is ResponseResult.Exception -> {
                        onFailure(result.e)
                    }
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


}