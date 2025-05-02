package com.ssafy.sotory.presentation.diary

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.presentation.diary.ui.WritingLottie
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryGenerationViewModel
import com.ssafy.sotory.presentation.diary.viewmodel.NavigationEvent


@Composable
fun DiaryWaitingScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryGenerationViewModel,
    onNavigateComplete: (diary: DiaryModel) -> Unit,
    onNavigateOtherCard: () -> Unit,
) {

    val generationState by viewModel.generationState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("DiaryWaitingScreen", "LaunchedEffect started")
        viewModel.navigationEvent.collect { event ->
            Log.d("DiaryWaitingScreen", "NavigationEvent: $event")
            when (event) {
                is NavigationEvent.ToCompletionScreen -> {
                    Log.d("DiaryWaitingScreen", "Navigating to completion screen")
                    onNavigateComplete(event.diary)
                }
                // 나머지 코드...
                is NavigationEvent.ToErrorScreen -> {
                }

                NavigationEvent.ToLoadingScreen -> {

                }
            }
        }
    }

    // 로그 확인용
    LaunchedEffect(generationState) {
        Log.d("DiaryWaitingScreen", "Generation state: $generationState")
    }



    Scaffold(topBar = {
        DefaultAppBar(
            title = "흔적 생성", canNavigateBack = canNavigateBack, navigateUp = navigateUp
        )
    }) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            WritingLottie(modifier = modifier.weight(1f))

            DefaultTextButton(onClick = {
                onNavigateOtherCard()
            }, content = "다른 사람 일기 카드 둘러보기")
        }
    }
}