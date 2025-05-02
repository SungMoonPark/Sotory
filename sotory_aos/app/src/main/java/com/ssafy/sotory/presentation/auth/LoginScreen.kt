package com.ssafy.sotory.presentation.auth

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ssafy.sotory.AuthViewModel
import com.ssafy.sotory.R
import com.ssafy.sotory.common.BottomRoute
import com.ssafy.sotory.common.Route
import com.ssafy.sotory.presentation.auth.viewmodel.LoginViewModel
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 185.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Image(
                painter = painterResource(R.drawable.sotory_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(220.dp)
            )

            Image(painter = painterResource(R.drawable.kakao_login_large_wide),
                contentDescription = "Kakao Login Button",
                modifier = Modifier
                    .noRippleClickable {
                        loginViewModel.kakaoLogin(
                            activity = activity,
                            onSuccess = { kakaoAccessToken ->
                                Log.d("LoginScreen", "카카오 어세스 토큰: $kakaoAccessToken")

                                loginViewModel.appLogin(
                                    kakaoAccessToken = kakaoAccessToken,
                                    onSuccess = {
                                        navController.navigate(BottomRoute.DiaryRoute) {
                                            popUpTo(Route.AuthBaseRoute.LoginRoute) { inclusive = true }
                                        }
                                    },
                                    onFailure = { error ->
                                        android.util.Log.e("LoginScreen", "서버 로그인 실패: $error")
                                    }
                                )

                            },
                            onFailure = { error ->
                                android.util.Log.e("LoginScreen", "카카오 로그인 실패: $error")
                            }
                        )
                    }
                    .size(300.dp)
            )
        }
    }
}