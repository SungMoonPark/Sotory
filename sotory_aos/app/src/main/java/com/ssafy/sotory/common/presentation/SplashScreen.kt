package com.ssafy.sotory.common.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.sotory.R
import com.ssafy.sotory.common.SplashRouteType
import com.ssafy.sotory.common.SplashViewModel

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToPermission: () -> Unit,
    splashViewModel: SplashViewModel = hiltViewModel<SplashViewModel>(),
    isLoggedIn: Boolean?,
) {
    val splashType by splashViewModel.splashRouteType

    LaunchedEffect(splashType, isLoggedIn) {
        when (splashType) {
            SplashRouteType.PERMISSION -> navigateToPermission()
            else -> {
                if (isLoggedIn != null) { // null이 아니면 로딩이 완료된 상태
//            delay(1500) // 스플래시 화면을 1.5초 동안 표시
                    navigateToHome()

                }
            }
        }
    }

    // 스플래시 화면 UI
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sotory_logo),
            contentDescription = "App Logo",
            Modifier.size(200.dp)
        )
    }


}
