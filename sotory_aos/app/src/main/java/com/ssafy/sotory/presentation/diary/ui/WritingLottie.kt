package com.ssafy.sotory.presentation.diary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.sotory.R
import com.ssafy.sotory.presentation.diary.LottieAnimationState
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.util.ScreenSizeUtil

@Composable
fun WritingLottie(
    modifier: Modifier = Modifier,
) {
    val screenSize = ScreenSizeUtil.getScreenSizeDp()
    val writingHeight = screenSize.second.value * 0.3

    // 1. 메모이제이션을 사용하여 컴포지션 재사용
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.writing)
    )

    // 2. 애니메이션 상태를 기억하여 재사용
    val animationState = remember {
        LottieAnimationState(
            isPlaying = true, speed = 1f, iterations = LottieConstants.IterateForever
        )
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(130.dp))
        Text("일기가 생성중이에요.", style = Heading_L_Bold)
        Spacer(modifier = Modifier.height(25.dp))
        Text("잠시만 기다려주세요.", style = Heading_M_SemiBold)
        // 3. 렌더링 로직 최적화
        Box(
            modifier = Modifier.height(writingHeight.dp), contentAlignment = Alignment.BottomCenter
        ) {
            // 4. 성능 개선된 LottieAnimation 사용
            LottieAnimation(
                composition = composition,
                isPlaying = animationState.isPlaying,
                iterations = animationState.iterations,
                speed = animationState.speed,
                contentScale = ContentScale.FillHeight
            )
        }
    }


}