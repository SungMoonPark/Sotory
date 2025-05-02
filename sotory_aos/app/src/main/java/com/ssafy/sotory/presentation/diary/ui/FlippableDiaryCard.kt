import CardSizeUtil.getDetailCardSize
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold


@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun FlippableDiaryCard(
    diaryModel: DiaryModel,
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // dp 단위의 화면 크기
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    // 카드 크기
    val detailCardSize = getDetailCardSize(screenWidthDp.value)
    val cardWidthDp = detailCardSize.width
    val cardHeightDp = detailCardSize.height

    // 픽셀 단위의 화면 크기
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    // 픽셀 단위의 카드 크기
    val cardWidthPx = with(density) { cardWidthDp.toPx() }
    val cardHeightPx = with(density) { cardHeightDp.toPx() }

    var swipeAmountX by remember { mutableStateOf(0f) }

    // 화면 크기 구하기
    var canvasSize by remember {
        mutableStateOf(
            IntSize(
                width = cardWidthPx.toInt(), height = screenHeightPx.toInt()
            )
        )
    }

    // 두 점의 좌표
    val Ypoint1 = remember { Offset(0f, -30f) }
    val Ypoint2 = remember { mutableStateOf(Offset(0f, 0f)) }

    // 두 점의 좌표
    val Xpoint1 = remember { Offset(0f, 30f) }
    val Xpoint2 = remember { mutableStateOf(Offset(0f, 0f)) }

    // 직선 방정식 계수 계산 결과
    val degreeYcoefficients = remember { mutableStateOf(Pair(0f, 0f)) } // (a, b) for y = ax + b
    val degreeXcoefficients = remember { mutableStateOf(Pair(0f, 0f)) } // (a, b) for y = ax + b


    // 화면 크기가 변경될 때마다 두 번째 점과 계수 업데이트
    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0) {
            // 두 번째 점의 x좌표를 화면 너비로 설정
            Ypoint2.value = Offset(canvasSize.width.toFloat(), 30f)
            Xpoint2.value = Offset(canvasSize.height.toFloat(), -30f)

            degreeYcoefficients.value = calculateLineCoefficients(Ypoint1, Ypoint2.value)
            degreeXcoefficients.value = calculateLineCoefficients(Xpoint1, Xpoint2.value)
        }
    }

    // 카드가 뒤집혔는지 상태 저장
    var isFlipped by remember { mutableStateOf(false) }

    // 스와이프 감지를 위한 변수들
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val swipeThreshold = 100f // 스와이프 감지 임계값

    // 목표 회전 값 상태 - 드래그로 계산된 실제 목표 값을 저장
    var targetRotationX by remember { mutableStateOf(0f) }
    var targetRotationY by remember { mutableStateOf(0f) }

    // 드래그 여부 상태
    var isDragging by remember { mutableStateOf(false) }

    // 회전 애니메이션 - rotationX와 rotationY가 부드럽게 변경되도록
    val animatedRotationX by animateFloatAsState(
        targetValue = targetRotationX, animationSpec = if (isDragging) {
            // 드래그 중에는 빠른 반응성을 위해 tween 사용
            tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        } else {
            // 드래그 종료 시 바운싱 효과를 위해 스프링 사용
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy, // 바운싱 정도
                stiffness = Spring.StiffnessLow // 스프링 강도
            )
        }, label = "rotationX"
    )

    val animatedRotationY by animateFloatAsState(
        targetValue = targetRotationY, animationSpec = if (isDragging) {
            // 드래그 중에는 빠른 반응성을 위해 tween 사용
            tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        } else {
            // 드래그 종료 시 바운싱 효과를 위해 스프링 사용
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy, // 바운싱 정도
                stiffness = Spring.StiffnessLow  // 스프링 강도
            )
        }, label = "rotationY"
    )

    var flipDegree by remember { mutableStateOf(0f) }

    // 완전 뒤집기 애니메이션 (클릭 시)
    val flipRotation by animateFloatAsState(
        targetValue = flipDegree, animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, // 바운싱 정도
            stiffness = Spring.StiffnessLow  // 스프링 강도
        ), label = "flipRotation"
    )

    val visibleDegree = abs(flipRotation) % 360
    val visibleBack = 90f < visibleDegree && visibleDegree < 270f

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 스케일 애니메이션
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, label = "scale"
    )

    // 변수 상태에 따라 애니메이션을 제어하는 로직
    LaunchedEffect(offsetX, offsetY) {
        if (offsetX == 0f && offsetY == 0f && !isDragging) {
            targetRotationX = 0f
            targetRotationY = 0f
        }
    }

    val context = LocalContext.current

    val cardModifier = Modifier
        .width(cardWidthDp)
        .height(cardHeightDp)
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                isFlipped = !isFlipped
            })
        }
        .pointerInput(Unit) {
            // 스와이프 감지
            var isSwiping = false

            detectDragGestures(onDragStart = { offset ->
                isSwiping = true
                isDragging = true
            }, onDragEnd = {
                // 스와이프 거리가 임계값을 넘으면 카드 뒤집기
                if (isSwiping && abs(swipeAmountX) > swipeThreshold) {
                    isFlipped = !isFlipped
                    if (swipeAmountX > 0) {
                        flipDegree += 180f
                    } else {
                        flipDegree -= 180f
                    }
                }
                swipeAmountX = 0f

                // 스와이프 완료 시 원위치
                offsetX = 0f
                offsetY = 0f

                // 드래그 모드 종료 - 스프링 바운싱 효과 활성화
                isDragging = false

                // 타겟 회전값은 애니메이션되며 0으로 돌아감
                targetRotationX = 0f
                targetRotationY = 0f

                isSwiping = false
            }, onDragCancel = {
                offsetX = 0f
                offsetY = 0f
                isSwiping = false
                isDragging = false

                // 타겟 회전값은 애니메이션되며 0으로 돌아감
                targetRotationX = 0f
                targetRotationY = 0f

                swipeAmountX = 0f
            }, onDrag = { change, dragAmount ->
                change.consume()

                swipeAmountX += dragAmount.x

                offsetX = change.position.x
                offsetY = change.position.y
                Log.d("Change Position", "x : ${change.position.x}, y : ${change.position.y}")

                val ya = degreeYcoefficients.value.first
                val yb = degreeYcoefficients.value.second

                val xa = degreeXcoefficients.value.first
                val xb = degreeXcoefficients.value.second

                // 목표 회전값 계산 - 애니메이션이 추적할 값
                targetRotationY = ya * offsetX + yb
                targetRotationX = xa * offsetY + xb
            })
        }

    // 뒷면
    Box(
        modifier = Modifier
            .width(cardWidthDp)
            .height(cardHeightDp)
            .graphicsLayer(
                rotationY = flipRotation - 180f + animatedRotationY, // 뒤집힌 카드의 회전,
                rotationX = animatedRotationX,
                cameraDistance = 12f * LocalDensity.current.density,
                alpha = if (visibleBack) 1f else 0f, // 90도 이상 회전했을 때만 보이게
            )
            .clip(RoundedCornerShape(16.dp))
            .background(color = BottomBarBackgroundColor)
            .padding(horizontal = 24.dp, vertical = 44.dp)
    ) {
        Text(text = diaryModel.summary, style = Heading_M_SemiBold)
    }
//    Image(
//        painter = painterResource(id = image),
//        contentDescription = "Local Image",
//        contentScale = ContentScale.FillBounds,
//        modifier = Modifier
//            .width(cardWidthDp)
//            .height(cardHeightDp)
////            .offset(x = initialOffset.x.dp, y = initialOffset.y.dp)
//            .graphicsLayer(
//                rotationY = flipRotation - 180f + animatedRotationY, // 뒤집힌 카드의 회전,
//                rotationX = animatedRotationX,
//                cameraDistance = 12f * LocalDensity.current.density,
//                alpha = if (visibleBack) 1f else 0f, // 90도 이상 회전했을 때만 보이게
//            )
//            .clip(RoundedCornerShape(16.dp)),
//    )
    // 앞면
    AsyncImage(
        model = ImageRequest.Builder(context).data(diaryModel.imgSrc).scale(Scale.FILL)
            .crossfade(true).build(),
        contentDescription = "Local Image",
        contentScale = ContentScale.FillBounds,
        modifier = cardModifier
            .graphicsLayer(
                rotationX = animatedRotationX, // 애니메이션된 X축 회전
                rotationY = flipRotation + animatedRotationY, // 애니메이션된 Y축 회전
                cameraDistance = 12f * LocalDensity.current.density,
                alpha = if (!visibleBack) 1f else 0f, // 90도 미만 회전했을 때만 보이게
            )
            .clip(RoundedCornerShape(16.dp))
    )
//    Image(
//        painter = painterResource(id = image),
//        contentScale = ContentScale.FillBounds,
//        contentDescription = "Local Image",
//        modifier = cardModifier
//            .graphicsLayer(
//                rotationX = animatedRotationX, // 애니메이션된 X축 회전
//                rotationY = flipRotation + animatedRotationY, // 애니메이션된 Y축 회전
//                cameraDistance = 12f * LocalDensity.current.density,
//                alpha = if (!visibleBack) 1f else 0f, // 90도 미만 회전했을 때만 보이게
//            )
//            .clip(RoundedCornerShape(16.dp))
//    )

}


/**
 * 두 점의 좌표를 이용하여 직선 방정식 y = ax + b의 계수 a와 b를 계산하는 함수
 *
 * @param point1 첫 번째 점의 좌표
 * @param point2 두 번째 점의 좌표
 * @return Pair<Float, Float> 첫 번째 요소는 기울기 a, 두 번째 요소는 y절편 b
 */
fun calculateLineCoefficients(point1: Offset, point2: Offset): Pair<Float, Float> {
    val x1 = point1.x
    val y1 = point1.y
    val x2 = point2.x
    val y2 = point2.y

    // 기울기 a 계산: (y2 - y1) / (x2 - x1)
    val a = if (x2 != x1) (y2 - y1) / (x2 - x1) else 0f

    // y절편 b 계산: y1 - a * x1
    val b = y1 - a * x1

    return Pair(a, b)
}
