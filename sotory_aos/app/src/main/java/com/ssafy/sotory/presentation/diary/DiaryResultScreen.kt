package com.ssafy.sotory.presentation.diary

import CardSizeUtil
import FlippableDiaryCard
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.data.dto.diary.Weather
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.presentation.diary.ui.DefaultBackgroundEffect
import com.ssafy.sotory.presentation.diary.ui.RainBackgroundEffect
import com.ssafy.sotory.presentation.diary.ui.SnowBackgroundEffect
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryGenerationViewModel
import com.ssafy.sotory.util.ScreenSizeUtil
import com.ssafy.sotory.util.formatDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DiaryResultScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    diary: DiaryModel,
    onNavigateHome: () -> Unit,
) {
    val title = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

//    val screenSize = ScreenSizeUtil.getScreenSizeDp()
//
//    val cardSize = CardSizeUtil.getDetailCardSize(screenSize.first.value)

    Scaffold(topBar = {
        DefaultAppBar(
            title = title, canNavigateBack = canNavigateBack, navigateUp = navigateUp,
            backgroundColor = Color.Transparent
        )
    }) { innerPadding ->

        Box(modifier = modifier.fillMaxSize()) {
            when (diary.weather) {
                Weather.RAIN -> RainBackgroundEffect()
                Weather.SNOW -> SnowBackgroundEffect()
                Weather.DEFAULT ->
                    DefaultBackgroundEffect()
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier.weight(1f), contentAlignment = Alignment.Center
            ) {
                FlippableDiaryCard(diary)
            }

            DefaultTextButton(
                content = "홈으로 가기", onClick = onNavigateHome
            )
        }


    }

}