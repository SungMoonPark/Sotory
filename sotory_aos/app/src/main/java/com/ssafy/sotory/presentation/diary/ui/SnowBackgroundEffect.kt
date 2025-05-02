package com.ssafy.sotory.presentation.diary.ui


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.Dispatchers
import kotlin.random.Random

@Composable
fun RealisticSnowAnimation(
    snowflakeCount: Int = 200  // 적절한 눈송이 수 조정
) {
    // 눈송이 클래스 정의
    class Snowflake {
        var x: Float = 0f
        var y: Float = 0f
        var size: Float = 0f
        var fallSpeed: Float = 0f
        var sway: Float = 0f     // 좌우 흔들림 정도
        var swaySpeed: Float = 0f // 흔들림 속도
        var rotation: Float = 0f  // 회전 각도
        var rotationSpeed: Float = 0f
        var swayOffset: Float = 0f  // 현재 흔들림 위치
        var type: Int = 0         // 눈송이 모양 타입
        var opacity: Float = 0f   // 투명도

        fun initialize(screenWidth: Float, screenHeight: Float) {
            x = Random.nextFloat() * screenWidth
            y = -30f + Random.nextFloat() * screenHeight

            // 크기 증가 (2-6에서 4-12로 변경)
            size = 4f + Random.nextFloat() * 8f

            // 속도 증가 (0.8-2.0에서 1.5-3.0으로 변경)
            fallSpeed = 1.5f + Random.nextFloat() * 1.5f

            sway = 0.5f + Random.nextFloat() * 1.5f
            swaySpeed = 0.005f + Random.nextFloat() * 0.015f
            rotation = Random.nextFloat() * 360f
            rotationSpeed = (Random.nextFloat() * 2f - 1f) * 0.3f
            swayOffset = Random.nextFloat() * 6.28f
            type = Random.nextInt(0, 6)

            // 투명도 감소하여 더 선명하게 (0.7-1.0에서 0.8-1.0으로 변경)
            opacity = 0.8f + Random.nextFloat() * 0.2f
        }
    }

    // 구성 및 밀도 정보
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    Log.d("SnowScreen", "screenWidthPx: $screenWidthPx, screenHeightPx: $screenHeightPx")
    // 바람 효과를 위한 상태
    var windStrength by remember { mutableStateOf(0f) }
    var windPhase by remember { mutableStateOf(0f) }

    // 렌더링 트리거
    var renderTrigger by remember { mutableStateOf(0L) }

    // 눈송이 모양 경로 (미리 계산)
    val snowflakePaths = remember {
        Array(4) { createSnowflakePath(it) }
    }

    // 눈송이 배열
    val snowflakes = remember {
        Array(snowflakeCount) { Snowflake() }
    }

    // 초기화
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            snowflakes.forEach { it.initialize(screenWidthPx, screenHeightPx) }
        }
    }

    // 애니메이션 업데이트
    LaunchedEffect(Unit) {
        var lastTime = System.currentTimeMillis()

        while(true) {
            delay(16) // 60fps

            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 1000f
            lastTime = currentTime

            // 바람 효과 업데이트
            windPhase += 0.003f
            windStrength = sin(windPhase) * 2f

            withContext(Dispatchers.Default) {
                // 모든 눈송이 업데이트
                for (flake in snowflakes) {
                    // 수직 이동
                    flake.y += flake.fallSpeed * 40f * deltaTime

                    // 회전
                    flake.rotation += flake.rotationSpeed

                    // 흔들림 위치 업데이트
                    flake.swayOffset += flake.swaySpeed * 50f * deltaTime

                    // 좌우 흔들림 + 바람 효과
                    val swayAmount = sin(flake.swayOffset) * flake.sway
                    val windEffect = windStrength * flake.size * 0.2f
                    flake.x += (swayAmount + windEffect) * deltaTime * 30f

                    // 화면 밖으로 나간 눈송이 재생성
                    if (flake.y > screenHeightPx + 20f) {
                        flake.y = -20f - Random.nextFloat() * 40f
                        flake.x = Random.nextFloat() * screenWidthPx
                    } else if (flake.x < -50f) {
                        flake.x = screenWidthPx + 10f
                    } else if (flake.x > screenWidthPx + 50f) {
                        flake.x = -10f
                    }
                }
            }

            renderTrigger = currentTime
        }
    }

    // 눈송이 그리기
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            @Suppress("UNUSED_EXPRESSION")
            renderTrigger

            for (flake in snowflakes) {
                // 화면에 보이는 눈송이만 그리기
                if (flake.y > -50f && flake.y < size.height + 50f &&
                    flake.x > -50f && flake.x < size.width + 50f) {

                    // 눈송이 회전 및 그리기
                    rotate(flake.rotation, Offset(flake.x, flake.y)) {
                        // 모양에 따라 다른 그리기 방식 적용
                        if (flake.type < 3) {
                            // 위치 및 크기 변환 후 눈송이 모양 그리기
                            translate(flake.x, flake.y) {
                                scale(flake.size / 10f) {
                                    drawPath(
                                        path = snowflakePaths[flake.type],
                                        color = Color.White.copy(alpha = flake.opacity)
                                    )
                                }
                            }
                        } else {
                            // 간단한 원형 눈송이
                            drawCircle(
                                color = Color.White.copy(alpha = flake.opacity),
                                radius = flake.size,
                                center = Offset(flake.x, flake.y)
                            )

                            // 흐릿한 외곽 효과
                            drawCircle(
                                color = Color.White.copy(alpha = flake.opacity * 0.3f),
                                radius = flake.size * 1.7f,
                                center = Offset(flake.x, flake.y)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 다양한 눈송이 모양 생성
private fun createSnowflakePath(type: Int): Path {
    val path = Path()

    when (type) {
        0 -> { // 6각형 눈결정
            val arms = 6
            val centerX = 0f
            val centerY = 0f
            val outerRadius = 10f
            val innerRadius = 4f

            // 중심점에서 6개의 가지 그리기
            for (i in 0 until arms) {
                val angle = (i * 2 * Math.PI / arms).toFloat()
                val x = centerX + cos(angle) * outerRadius
                val y = centerY + sin(angle) * outerRadius

                path.moveTo(centerX, centerY)
                path.lineTo(x, y)

                // 가지 중간에 작은 가지 추가
                val middleX = centerX + cos(angle) * innerRadius
                val middleY = centerY + sin(angle) * innerRadius

                val angle1 = angle + Math.PI.toFloat() / 6
                val angle2 = angle - Math.PI.toFloat() / 6

                val branch1X = middleX + cos(angle1) * innerRadius
                val branch1Y = middleY + sin(angle1) * innerRadius
                val branch2X = middleX + cos(angle2) * innerRadius
                val branch2Y = middleY + sin(angle2) * innerRadius

                path.moveTo(middleX, middleY)
                path.lineTo(branch1X, branch1Y)

                path.moveTo(middleX, middleY)
                path.lineTo(branch2X, branch2Y)
            }
        }
          1 -> { // 별 모양 눈송이
            val points = 8
            val centerX = 0f
            val centerY = 0f
            val outerRadius = 10f
            val innerRadius = 4f

            path.moveTo(
                centerX + cos(0f) * outerRadius,
                centerY + sin(0f) * outerRadius
            )

            for (i in 1..points * 2) {
                val angle = (i * Math.PI / points).toFloat()
                val radius = if (i % 2 == 0) outerRadius else innerRadius

                path.lineTo(
                    centerX + cos(angle) * radius,
                    centerY + sin(angle) * radius
                )
            }

            path.close()
        }
        2-> { // 원 모양 눈송이
            val centerX = 0f
            val centerY = 0f
            val radius = 5f

            // 중앙 원
            path.addOval(androidx.compose.ui.geometry.Rect(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            ))

            // 작은 돌기들 추가
            val bumps = 8
            val bumpRadius = radius * 0.4f

            for (i in 0 until bumps) {
                val angle = (i * 2 * Math.PI / bumps).toFloat()
                val x = centerX + cos(angle) * (radius + bumpRadius / 2)
                val y = centerY + sin(angle) * (radius + bumpRadius / 2)

                path.addOval(androidx.compose.ui.geometry.Rect(
                    x - bumpRadius,
                    y - bumpRadius,
                    x + bumpRadius,
                    y + bumpRadius
                ))
            }
        }
        else -> { // 단순한 원형 눈송이 (fallback)
            path.addOval(androidx.compose.ui.geometry.Rect(
                -5f, -5f, 5f, 5f
            ))
        }
    }

    return path
}

// 메인 화면에서 사용 예시
@Composable
fun SnowBackgroundEffect() {
    RealisticSnowAnimation(
        snowflakeCount = 200  // 적절한 수 조정
    )
}