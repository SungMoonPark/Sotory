package com.ssafy.sotory.presentation.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.dialog.TwoButtonDialog
import com.ssafy.sotory.domain.userinfo.Gender
import com.ssafy.sotory.presentation.payment.viewmodel.NavigationAction
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentHistoryViewModel
import com.ssafy.sotory.presentation.settings.ui.SettingsMainMenu
import com.ssafy.sotory.presentation.settings.ui.SettingsProfileCard
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainViewModel
import com.ssafy.sotory.ui.theme.Black400
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import kotlinx.coroutines.launch

//SettingsMainScreen(
//canNavigateBack = navController.previousBackStackEntry != null,
//navigateUp = { navController.popBackStack() },
//)

@Composable
fun SettingsMainScreen(
    canNavigateBack: Boolean,
    onNavigate: (SettingsMainNav) -> Unit,
//    drawerState: DrawerState,
    navigateUp: () -> Unit,
    viewModel: SettingsMainViewModel = hiltViewModel(),
) {
    val appbarTitle = "마이룸"
    val isLogOutDialogOpen by viewModel.isLogOutDialogOpen.collectAsState()
    val scope = rememberCoroutineScope()

    val userInfoState by viewModel.userInfo.collectAsState()
    Log.d("SettingsMainScreen", "userInfoState의 값은 ${userInfoState}")

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
    }

    LaunchedEffect(key1 = true) {

        viewModel.settingsMainNav.collect { action ->
            when (action) {
                is SettingsMainNav.ToBack -> {
                    onNavigate(action)
                }

                is SettingsMainNav.ToEditProfile -> {
                    onNavigate(action)
                    viewModel.fetchUserInfo()
                }

                is SettingsMainNav.ToListCard -> {
                    onNavigate(action)
                }

                is SettingsMainNav.ToSetAlarm -> {
                    onNavigate(action)
                }

                is SettingsMainNav.ToTerms -> {
                    onNavigate(action)
                }

                is SettingsMainNav.ToLogOut -> {
                    onNavigate(action)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .zIndex(1f)
            .fillMaxSize()
            .background(
                color = if (isLogOutDialogOpen) Black400.copy(alpha = 0.5f) else Color.Transparent
            )
    ) {
        if (isLogOutDialogOpen) {
            TwoButtonDialog(
                content = "로그아웃 하시겠습니까?",
                onCancel = {
                    viewModel.onEvent(event = SettingsMainEvent.ClickToLogOutDismiss)
                },
                onConfirm = {
                    viewModel.onEvent(event = SettingsMainEvent.ClickToLogOutConfirm)
                }
            )
        }
    }

    Scaffold(topBar = {
        DefaultAppBar(
            title = appbarTitle, canNavigateBack = canNavigateBack, navigateUp = navigateUp
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(vertical = 30.dp)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userInfoState?.let { userInfo ->
                SettingsProfileCard(
                    userName = userInfo.nickname,
                    birthDay = userInfo.birthday?.replace("-", "."),
                    gender = when (userInfo.gender) {
                        Gender.M -> "남성"
                        Gender.F -> "여성"
                        else -> "성별 비공개"
                    },
                    onClick = {
                        viewModel.onEvent(event = SettingsMainEvent.EditProfile)
                    }
                )
            } ?: Text("프로필 정보를 불러오는 중입니다...")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "연동 카드 관리", style = Heading_S_SemiBold)

                SettingsMainMenu(
                    text = "내 카드 목록",
                    onClick = {
                        viewModel.onEvent(event = SettingsMainEvent.ClickToListCard)
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "기타", style = Heading_S_SemiBold)

//                SettingsMainMenu(
//                    text = "알림 설정",
//                    onClick = {
//                        viewModel.onEvent(event = SettingsMainEvent.ClickToSetAlarm)
//                    }
//                )
                SettingsMainMenu(
                    text = "약관",
                    onClick = {
                        viewModel.onEvent(event = SettingsMainEvent.ClickToReadTerms)
                    }
                )

//                SettingsMainMenu(
//                    text = "로그아웃", onClick = {
//                        Log.d("TEST_CLICK", "로그아웃 버튼 눌림")
//                        viewModel.onEvent(event = SettingsMainEvent.ClickToLogOut)
//                        Log.d("LogOut", "message: logout 버튼 누름")
//                    })
            }
        }
    }
}