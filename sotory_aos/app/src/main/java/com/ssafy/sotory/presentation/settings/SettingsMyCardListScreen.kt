package com.ssafy.sotory.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.dialog.TwoButtonDialog
import com.ssafy.sotory.presentation.settings.ui.MyCardList
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListViewModel
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainNav
import com.ssafy.sotory.ui.theme.Black400
import com.ssafy.sotory.ui.theme.Display_L_Bold
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun SettingsMyCardListScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    viewModel: SettingsCardListViewModel,
    onNavigate: (SettingsCardListNav) -> Unit,
){

    // 연결된 나의 카드 목록

    LaunchedEffect(Unit) {
        viewModel.fetchConnectedCards()
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.settingsCardListNav.collect { action ->
            when (action) {
                is SettingsCardListNav.ToAddCardToConnect -> {
                    onNavigate(action)
                }

                is SettingsCardListNav.ReturnToMyCard -> {
                    onNavigate(action)
                }
            }
        }
    }

    // 카드 추가하는 페이지
    // 연결 되어있는 카드 목록 출력
    val appbarTitle = "카드 연결"
    val isDeleteCardDialogOpen by viewModel.isDeleteCardDialogOpen.collectAsState()
    val targetCardId = viewModel.targetDeleteCardId.collectAsState()
    val targetCardName by viewModel.targetDeleteCardName.collectAsState()

    Box(
        modifier = Modifier
            .zIndex(1f)
            .fillMaxSize()
            .background(
                color = if (isDeleteCardDialogOpen) Black400.copy(alpha = 0.5f) else Color.Transparent
            )
    ) {
        if (isDeleteCardDialogOpen) {
            TwoButtonDialog(
                content = "${targetCardName}를 삭제하시겠습니까?",
                onCancel = {
                    viewModel.onEvent(event = SettingsCardListEvent.ClickDeleteCardDialogDismiss)
                },
                onConfirm = {
                        targetCardId.value?.let { id ->
                            viewModel.onEvent(SettingsCardListEvent.ClickDeleteCardDialogCheck(id))
                        }
                }
            )
        }
    }

    Scaffold(topBar = {
        DefaultAppBar(
            title = appbarTitle,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
            actions = {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "추가",
                        style = Heading_S_SemiBold,
                        modifier = Modifier
                            .noRippleClickable {
                                viewModel.onEvent(event = SettingsCardListEvent.AddCardToConnect)
                            }
                    )
                }
            }
        )
    }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    text = "내 카드 목록",
                    style = Display_L_Bold
                )

                Text(
                    text = "일기에 사용되는 소비 내역을\n불러올 수 있는 카드 목록이에요.",
                    style = Heading_S_Medium
                )
            }

            MyCardList(viewModel)
        }
    }
}