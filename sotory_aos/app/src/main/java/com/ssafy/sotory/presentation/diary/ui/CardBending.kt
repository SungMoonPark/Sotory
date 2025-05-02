package com.ssafy.sotory.presentation.diary.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

//@Composable
//fun TrueCurvedCard_SyncedAnimation_WithRoundedCorners(image: ImageBitmap) {
//
//    var isPressed by remember { mutableStateOf(false) }
//
//    // progress: 0f → 1f로 자연스럽게 변화
//    val animationProgress by animateFloatAsState(
//        targetValue = if (isPressed) 1f else 0f,
//        animationSpec = tween(400),
//        label = "animationProgress"
//    )
//
//    // 곡률과 높이 동기화
//    val curveAmount = animationProgress * 40f
//    val originalHeight = 350.dp
//    val reducedHeight = originalHeight - (curveAmount.dp * 0.8f)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5)),
//        contentAlignment = Alignment.Center
//    ) {
//        Canvas(
//            modifier = Modifier
//                .size(250.dp, reducedHeight)
//                .pointerInput(Unit) {
//                    awaitPointerEventScope {
//                        while (true) {
//                            val event = awaitPointerEvent()
//                            isPressed = event.changes.any { it.pressed }
//                        }
//                    }
//                }
//        ) {
//            val w = size.width
//            val h = size.height
//            val curve = curveAmount
//            val cornerRadius = 20.dp.toPx()
//
//            val path = Path().apply {
//                moveTo(curve + cornerRadius, 0f)
//
//                lineTo(w - curve - cornerRadius, 0f)
//                quadraticBezierTo(w - curve, 0f, w - curve, cornerRadius)
//
//                cubicTo(
//                    w, h * 0.25f,
//                    w, h * 0.75f,
//                    w - curve, h - cornerRadius
//                )
//                quadraticTo(w - curve, h, w - curve - cornerRadius, h)
//
//                lineTo(curve + cornerRadius, h)
//                quadraticTo(curve, h, curve, h - cornerRadius)
//
//                cubicTo(
//                    0f, h * 0.75f,
//                    0f, h * 0.25f,
//                    curve, cornerRadius
//                )
//                quadraticTo(curve, 0f, curve + cornerRadius, 0f)
//
//                close()
//            }
//
//            // 배경 이미지
//            clipPath(path) {
//                drawImage(
//                    image = image,
//                    dstSize = IntSize(w.toInt(), h.toInt())
//                )
//            }
//
//            // 명암
//            drawPath(
//                path = path,
//                brush = Brush.radialGradient(
//                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.25f)),
//                    center = Offset(w / 2, h / 2),
//                    radius = w
//                ),
//                style = Fill
//            )
//
//            // 외곽선
//            drawPath(
//                path = path,
//                color = Color.Black,
//                style = Stroke(width = 2f)
//            )
//        }
//    }
//}

@Composable
fun SwipeDiaryCard(
    image: ImageBitmap,
    isDragged: Boolean = false,
) {
    // 화면 너비 가져오기
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp


    // 카드 너비를 화면의 60%에 맞게 설정 (패딩 고려)
    val cardWidth = screenWidth * 0.6f
    val cardHeight = screenHeight * 0.48f

    val context = LocalContext.current

//    var isPressed by remember { mutableStateOf(false) }

    val animationProgress by animateFloatAsState(
        targetValue = if (isDragged) 1f else 0f,
        animationSpec = tween(400),
        label = "animationProgress"
    )

    val curveAmount = animationProgress * 40f
    val reducedHeight = cardHeight - (curveAmount.dp * 0.8f)


    Canvas(
        modifier = Modifier
            .size(cardWidth, reducedHeight)
    ) {
        val w = size.width
        val h = size.height
        val cornerRadius = 20.dp.toPx()

        val path = Path().apply {
            moveTo(curveAmount + cornerRadius, 0f)
            lineTo(w - curveAmount - cornerRadius, 0f)
            quadraticTo(w - curveAmount, 0f, w - curveAmount, cornerRadius)
            cubicTo(w, h * 0.25f, w, h * 0.75f, w - curveAmount, h - cornerRadius)
            quadraticTo(w - curveAmount, h, w - curveAmount - cornerRadius, h)
            lineTo(curveAmount + cornerRadius, h)
            quadraticTo(curveAmount, h, curveAmount, h - cornerRadius)
            cubicTo(0f, h * 0.75f, 0f, h * 0.25f, curveAmount, cornerRadius)
            quadraticTo(curveAmount, 0f, curveAmount + cornerRadius, 0f)
            close()
        }

        clipPath(path) {
            drawImage(
                image = image, dstSize = IntSize(w.toInt(), h.toInt())
            )
        }

        drawPath(
            path = path, brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.25f)),
                center = Offset(w / 2, h / 2),
                radius = w
            ), style = Fill
        )

        drawPath(
            path = path, color = Color.Black, style = Stroke(width = 2f)
        )
    }
}
