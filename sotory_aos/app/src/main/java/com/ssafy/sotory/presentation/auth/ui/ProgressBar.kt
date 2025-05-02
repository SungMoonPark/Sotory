package com.ssafy.sotory.presentation.auth.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ProgressBar(
    currentStep: Int, // 1~3
    totalSteps: Int = 3,
    modifier: Modifier = Modifier
) {
    val targetProgress = currentStep / totalSteps.toFloat()

    // 한 번만 생성됨
    val animatedProgress = remember { Animatable(0f) }

    // currentStep이 바뀔 때 애니메이션 실행
    LaunchedEffect(currentStep) {
        animatedProgress.animateTo(
            targetProgress,
            animationSpec = tween(
                durationMillis = 600,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(animatedProgress.value.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(Color(0xFF9F87FF))
        )
    }
}




//@Composable
//fun ProgressBar(
//    currentStep: Int, // 1~3
//    totalSteps: Int = 3,
//    modifier: Modifier = Modifier
//) {
//    val progress by animateFloatAsState(
//        targetValue = currentStep / totalSteps.toFloat(),
//        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
//        label = "Step Progress Animation"
//    )
//
//    Box(
//        modifier
//            .fillMaxWidth()
//            .height(4.dp)
//            .background(Color.White.copy(alpha = 0.3f)) // 배경 바 (흰색의 반투명)
//    ) {
//        Box(
//            Modifier
//                .fillMaxWidth(fraction = progress)
//                .fillMaxHeight()
//                .background(Color(0xFF9F87FF)) // 보라색 진행 바
//        )
//    }
//}