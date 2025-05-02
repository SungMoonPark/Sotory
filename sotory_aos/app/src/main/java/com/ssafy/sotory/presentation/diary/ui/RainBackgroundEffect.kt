package com.ssafy.sotory.presentation.diary.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.Black200
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RainAnimation(
    raindropsCount: Int = 300,
) {
    // 가변 클래스 사용으로 객체 생성 최소화
    class MutableRaindrop(
        var x: Float,
        var y: Float,
        val speed: Float,
        val length: Float,
        val thickness: Float,
    )

    // 구성 및 밀도 정보
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // 렌더링용 상태는 분리
    var renderTrigger by remember { mutableStateOf(0L) }

    // 빗방울 데이터는 remember로 유지만 하고 상태로 관리하지 않음
    val raindrops = remember {
        Array(raindropsCount) {
            MutableRaindrop(
                x = Random.nextFloat() * screenWidthPx,
                y = Random.nextFloat() * screenHeightPx,
                speed = 60f + Random.nextFloat() * 60f,
                length = 100f + Random.nextFloat() * 100f,
                thickness = 0.2f + Random.nextFloat() * 1.0f
            )
        }
    }

    // 애니메이션 로직 - 백그라운드에서 처리
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // 약 60fps

            // 빗방울 위치 업데이트 (상태 변경 없음)
            for (drop in raindrops) {
                // 빗방울을 아래로 이동
                drop.y += drop.speed

                // 화면 밖으로 나간 빗방울 재생성
                if (drop.y > screenHeightPx) {
                    drop.x = Random.nextFloat() * screenWidthPx
                    drop.y = 0f
                }
            }

            // 렌더링 트리거만 상태로 업데이트
            renderTrigger = System.currentTimeMillis()
        }
    }

    // 배경과 빗방울 그리기
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // renderTrigger 변경될 때 다시 그림
            @Suppress("UNUSED_EXPRESSION") renderTrigger

            raindrops.forEach { drop ->
                // 화면 안에 있는 빗방울만 그리기
                if (drop.y > -drop.length && drop.x < width + drop.thickness) {
                    // 빗방울을 여러 단계로 나누어 그리기
                    val segments = 5 // 빗방울을 5개 세그먼트로 나눔

                    // 세그먼트의 총 비율 합계 계산 (1 + 0.8 + 0.6 + 0.4 + 0.2 = 3)
                    val totalRatio = (1..segments).sumOf { (segments - it + 1) * 0.2 }.toFloat()

                    // 각 세그먼트의 비율 배열 (예: [0.33, 0.27, 0.2, 0.13, 0.07])
                    val segmentRatios = FloatArray(segments) { i ->
                        ((segments - i) * 0.2f) / totalRatio
                    }

                    var currentY = drop.y

                    // 각 세그먼트 그리기
                    for (i in 0 until segments) {
                        val segmentLength = drop.length * segmentRatios[i]
                        val startY = currentY
                        val endY = startY + segmentLength

                        // 위에서 아래로 갈수록 알파값 증가 (0.2에서 1.0까지)
                        val alphaRatio = (i + 1).toFloat() / segments
                        val alpha = 0.2f + (0.8f * alphaRatio)

                        drawLine(color = Black200.copy(alpha = alpha),
                            cap = StrokeCap.Round,
                            start = Offset(drop.x, startY),
                            end = Offset(drop.x, endY),
                            strokeWidth = with(density) { drop.thickness.dp.toPx() })

                        // 다음 세그먼트의 시작 위치 업데이트
                        currentY = endY
                    }
                }
            }
        }
    }
}

// 메인 화면에서 사용 예시
@Composable
fun RainBackgroundEffect() {
    RainAnimation()
}