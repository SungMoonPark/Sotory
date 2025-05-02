package com.ssafy.sotory.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.presentation.settings.ui.AddingCardList
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListViewModel
import com.ssafy.sotory.ui.theme.Display_L_Bold
import com.ssafy.sotory.ui.theme.Heading_S_Medium

@Composable
fun SettingsAddingCardScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    viewModel: SettingsCardListViewModel,
    onNavigate: (SettingsCardListNav) -> Unit,
){
    // 보유하고 있는 나의 카드 목록

    val appbarTitle = "내 카드 목록"
    val selectedCardNos by viewModel.selectedCardNos.collectAsState()

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

    LaunchedEffect(Unit) {
        viewModel.fetchOwnedCards()
        viewModel.fetchConnectedCards()
    }

    Scaffold(topBar = {
        DefaultAppBar(
            title = appbarTitle,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
        )
    }) { innerPadding ->

        Column(
            modifier = Modifier
//                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    Text(
                        text = "카드 연결",
                        style = Display_L_Bold
                    )

                    Text(
                        text = "일기에 사용되는 소비 내역과\n연동할 카드 목록이에요.",
                        style = Heading_S_Medium
                    )

                    AddingCardList(viewModel)
                }
            }
            BottomOneButton(
                text = "추가하기",
                enabled = selectedCardNos.isNotEmpty(),
                onClick = {
                    viewModel.onEvent(event = SettingsCardListEvent.ClickAddCards)
                },
            )
        }
    }
}

