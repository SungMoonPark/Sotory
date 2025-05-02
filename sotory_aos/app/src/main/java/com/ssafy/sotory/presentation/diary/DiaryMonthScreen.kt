package com.ssafy.sotory.presentation.diary

import CardSizeUtil
import CardSizeUtil.getDailyCardSize
import CardSizeUtil.getDetailCardSize
import FlippableDiaryCard
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import coil3.toBitmap
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.data.dto.diary.Weather
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.domain.diary.EmptyDiaryModel
import com.ssafy.sotory.presentation.diary.ui.DefaultBackgroundEffect
import com.ssafy.sotory.presentation.diary.ui.RainBackgroundEffect
import com.ssafy.sotory.presentation.diary.ui.SnowBackgroundEffect
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryMonthViewModel
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryUiState
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.Display_L_Bold
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable
import com.ssafy.sotory.util.ScreenSizeUtil
import com.ssafy.sotory.util.SystemBarUtil
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sin

// 카드 상태를 나타내는 enum 클래스
enum class CardState {
    IDLE,           // 기본 상태
    DRAGGING,       // 드래그 중인 상태
    MAX_DRAGGED,    // 최대 드래그 상태
    FLYING,         // 날아가는 상태
    COMPLETED       // 완료 상태
}

enum class ScreenStep {
    MONTH_CARD, FROM_DATE_CARD_TO_MONTH_CARD, FROM_MONTH_CARD_TO_DATE_CARD, DATE_CARD, FROM_DETAIL_TO_DATE_CARD, FROM_DATE_CARD_TO_DETAIL, DETAIL
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun DiaryDateScreen(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    diaryMonthViewModel: DiaryMonthViewModel = hiltViewModel(),
    onNavigatePayment: (String) -> Unit,
    onNavigateForm: () -> Unit,
) {
    val uiState by diaryMonthViewModel.diaryMonthUiState.collectAsStateWithLifecycle()
    val year by diaryMonthViewModel.year.collectAsStateWithLifecycle()
    val selectedMonth by diaryMonthViewModel.month.collectAsStateWithLifecycle()
    val screenState by diaryMonthViewModel.screenState.collectAsStateWithLifecycle()
    val currentDay by diaryMonthViewModel.currentDay.collectAsStateWithLifecycle()

    val dateDiaries by diaryMonthViewModel.dateDiaries.collectAsStateWithLifecycle()
    Log.d("LaunchedEffect", "dateDiaries = ${dateDiaries.size}")
    Log.d("LaunchedEffect", "screenState = ${screenState}")
    val maxDateCardCount by diaryMonthViewModel.maxDateCardCount.collectAsStateWithLifecycle()

    val fromWindowHeight = SystemBarUtil.getStatusBarHeightFromWindow()


    val density = LocalDensity.current
    val context = LocalContext.current

    val scrollState = rememberLazyGridState()

    val screenSizeDp = ScreenSizeUtil.getScreenSizeDp()
    val screenSizePx = ScreenSizeUtil.getScreenSizePx()

    val screenWidthPx = screenSizePx.first
    val screenHeightPx = screenSizePx.second

    var visibility by remember { mutableStateOf(1f) }

    // 카드 너비를 화면의 60%로 설정
    val cardWidthDp = screenSizeDp.first * 0.6f

    val flipCardWidthPx = screenWidthPx * 2 / 3


    // 양 옆에 카드가 더 많이 보이도록 패딩 설정
    // 중앙 정렬을 위해 (전체 화면 - 카드 너비) / 2 계산
    val horizontalPadding = (screenSizeDp.first - cardWidthDp) / 2


    // 페이지별 상태 관리
    val cardStateMap = remember { mutableStateMapOf<Int, CardState>() }

    // 페이지별 드래그 상태를 mutableStateMapOf로 관리하여 값 변경 시 리컴포지션 발생
    val dragChargingMap = remember { mutableStateMapOf<Int, Float>() }


    // 위에서 중앙으로 내려오는 애니메이션 구현
    val slideInAnimationSpec = tween<Float>(
        durationMillis = 500, easing = FastOutSlowInEasing
    )


    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val detailCardSize = getDetailCardSize(screenWidthDp.value)
    // 카드 크기
    val detailCardHeight = detailCardSize.height
    // 애니메이션 초기 위치 (화면 위쪽 밖에서 시작)
    val initialOffset = -screenHeightPx
    val baseTranslationY = (screenHeightPx - detailCardHeight.value * density.density) / 2

    // 애니메이션된 Y 오프셋
    val slidingYOffset by animateFloatAsState(
        targetValue = if (screenState == ScreenStep.DETAIL) baseTranslationY else initialOffset.toFloat(),
        animationSpec = slideInAnimationSpec,
        label = "slidingAnimation"
    )

    // 애니메이션된 Alpha 값
    val cardAlpha by animateFloatAsState(
        targetValue = if (screenState == ScreenStep.DETAIL) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alphaAnimation"
    )


    // 애니메이션된 스케일 값
    val cardScale by animateFloatAsState(
        targetValue = if (screenState == ScreenStep.DETAIL) 1f else 0.8f,
        animationSpec = slideInAnimationSpec,
        label = "scaleAnimation"
    )

    // Add a new animatable for fade transition
    val dateFadeAnimatable = remember { Animatable(0f) }
    val monthFadeAnimatable = remember { Animatable(1f) }
    val dateTranslationYAnimatable = remember { Animatable(screenHeightPx / 2) }
    val monthTranslationYAnimatable = remember { Animatable(0f) }
    val monthTranslationXAnimatable = remember { Animatable(0f) }
    val monthCardFadeAnimatable = remember { Animatable(1f) }
    val monthScaleAnimatable = remember { Animatable(1f) }

    val dailyCardSize = getDailyCardSize(screenSizeDp.first.value, screenSizeDp.second.value)
//    if (!isCurrentPage) {
//        translationX =
//            (dailyCardSize.width.value * density.density * 1f) * (pagerState.currentPage - page) // * pageOffset.coerceIn(-1f, 1f)
//    }
    val dailyInitialXAnimatable =
        remember { Animatable((dailyCardSize.width.value * density.density * 1f)) }
    val dailyInitialYAnimatable = remember { Animatable(0f) }
    val dailyRotateZAnimatable = remember { Animatable(0f) }

    // 뒤로가기 버튼이 눌린 시간을 추적
    var backPressedTime by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()


// SlowOutFastInEasing 구현 - FastOutSlowInEasing의 반대 동작
    val SlowOutFastInEasing = CubicBezierEasing(0.8f, 0.0f, 0.2f, 1.0f)

    // Add LaunchedEffect to handle the fade animation when state changes
    LaunchedEffect(screenState) {
        when (screenState) {
            // 월별 -> 일별
            ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD -> {
                coroutineScope {
                    launch {
                        monthFadeAnimatable.snapTo(
                            targetValue = 0f,
                        )
                    }
                }

                coroutineScope {
                    launch {
                        monthCardFadeAnimatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(800, easing = SlowOutFastInEasing)
                        )
                    }
                    launch {
                        dateFadeAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        dateTranslationYAnimatable.snapTo(
                            targetValue = 0f,
//                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
                    }

                    launch {
                        dailyInitialXAnimatable.animateTo(
                            targetValue = (screenSizeDp.first.value * 0.48f),
                            animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        dailyRotateZAnimatable.animateTo(
                            targetValue = -13f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        dailyInitialYAnimatable.animateTo(
                            targetValue = screenSizeDp.second.value * 0.5f,
                            animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                }
                diaryMonthViewModel.updateScreenState(
                    ScreenStep.DATE_CARD
                )
            }

            // 일별 -> 월별
            ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD -> {
                coroutineScope {
                    launch {
                        dailyInitialXAnimatable.animateTo(
                            targetValue = (dailyCardSize.width.value * density.density * 1f),
                            animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        dailyRotateZAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        dailyInitialYAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }

                    launch {
                        dateFadeAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }

                    launch {
                        monthCardFadeAnimatable.snapTo(
                            targetValue = 0f,
                        )
                        monthCardFadeAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(500, easing = LinearEasing)
                        )
                    }
                }
                coroutineScope {
                    launch {
                        monthFadeAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        monthTranslationYAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        monthTranslationXAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                    launch {
                        monthScaleAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                }

                diaryMonthViewModel.updateScreenState(
                    ScreenStep.MONTH_CARD
                )
            }
            // 일별 -> 상세
            ScreenStep.FROM_DATE_CARD_TO_DETAIL -> {
                coroutineScope {
                    launch {
                        // 1단계: 페이드 아웃과 이동 애니메이션 병렬 실행
                        coroutineScope {
                            launch {
                                dateFadeAnimatable.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(300, easing = LinearEasing)
                                )
                            }
                            launch {
                                dateTranslationYAnimatable.animateTo(
                                    targetValue = screenHeightPx.toFloat() / 2,
                                    animationSpec = tween(300, easing = LinearEasing)
                                )
                            }
                        }
                        diaryMonthViewModel.updateScreenState(
                            ScreenStep.DETAIL
                        )
                        // 이 시점에서 위 두 애니메이션은 모두 완료됨


                        // 3단계: 페이드 인 애니메이션 실행
                        dateFadeAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(300, easing = LinearEasing)
                        )

                    }
                }
            }

            // 상세 -> 일별
            ScreenStep.FROM_DETAIL_TO_DATE_CARD -> {
                // Start fade out animation
                coroutineScope {
                    launch {
                        dateFadeAnimatable.animateTo(
                            targetValue = 0f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }

                }

                // Once fade out is complete, change state to SWIPE
                // Fade back in
                coroutineScope {
                    launch {
                        dateTranslationYAnimatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
                    }
                    launch {
                        dateFadeAnimatable.animateTo(
                            targetValue = 1f, animationSpec = tween(300, easing = LinearEasing)
                        )
                    }
                }
                diaryMonthViewModel.updateScreenState(
                    ScreenStep.DATE_CARD
                )
            }


            else -> {
                // For other states, ensure we're fully visible
//                dateFadeAnimatable.snapTo(1f)
            }
        }
    }

    // 아이템별 위치 정보를 저장할 Map 추가
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }


    val monthCards = listOf(
        R.drawable.m1,
        R.drawable.m2,
        R.drawable.m3,
        R.drawable.m4,
        R.drawable.m5,
        R.drawable.m6,
        R.drawable.m7,
        R.drawable.m8,
        R.drawable.m9,
        R.drawable.m10,
        R.drawable.m11,
        R.drawable.m12,
    )

    val monthlyCardSize =
        CardSizeUtil.getMonthlyCardSize(screenSizeDp.first.value, screenSizeDp.second.value)

    // Modify BackHandler to handle fade transition
    BackHandler(screenState != ScreenStep.MONTH_CARD) {
        Log.d("BackHandler", "BackHandler = $screenState")
        if (screenState == ScreenStep.DETAIL) {
            // Change to TO_SWIPE state instead of immediately to SWIPE
            diaryMonthViewModel.updateScreenState(
                ScreenStep.FROM_DETAIL_TO_DATE_CARD
            )
        } else if (screenState == ScreenStep.DATE_CARD) {
            diaryMonthViewModel.updateScreenState(
                ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD
            )
        } else if (screenState == ScreenStep.MONTH_CARD) {
            val currentTime = System.currentTimeMillis()

            // 2초 이내에 두 번 뒤로가기 버튼이 눌렸는지 확인
            if (currentTime - backPressedTime < 2000) {
                // 앱 종료
                (context as? Activity)?.finish()
            }
        }
    }

    when (uiState) {
        is DiaryUiState.Loading -> {
//            SnowScreen()
        }

        is DiaryUiState.Error -> {
            Text(text = (uiState as DiaryUiState.Error).message)
        }

        is DiaryUiState.Success -> {
            val diaryMonthModels = (uiState as DiaryUiState.Success).payments


//            Log.d(
//                "LaunchedEffect",
//                "month = $selectedMonth, diaryMonthModels Size = ${diaryMonthModels.size},  initialPage = ${diaryMonthModels[selectedMonth - 1].count!! - 1}"
//            )
//            for (i in 0 until diaryMonthModels.size) {
//                Log.d(
//                    "LaunchedEffect", "month [${i + 1}] ${diaryMonthModels[i].count}"
//                )
//            }
            var pagerState = rememberPagerState(
                initialPage = diaryMonthModels[selectedMonth - 1].count!! - 1,
                initialPageOffsetFraction = 0f
            ) {
                val now = LocalDate.now()
                val nowYear = now.year
                val nowMonth = now.month.value
                val nowDay = now.dayOfMonth
                val existDailyCard =
                    nowYear == year && nowMonth == selectedMonth && nowDay == diaryMonthModels[selectedMonth - 1].count!!
                // 그 월에 최대 갯수일 경우
                if (maxDateCardCount == diaryMonthModels[selectedMonth - 1].count!! || existDailyCard) diaryMonthModels[selectedMonth - 1].count!!
                else diaryMonthModels[selectedMonth - 1].count!! + 1
            }

            // 커스텀 스냅 플링 동작 정의
            var flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                // 스프링 애니메이션 스펙 사용
                snapAnimationSpec = SpringSpec(
                    dampingRatio = 0.8f, stiffness = 400f
                )
            )
            key(selectedMonth) {
                pagerState = rememberPagerState(
                    initialPage = diaryMonthModels[selectedMonth - 1].count!! - 1,
                    initialPageOffsetFraction = 0f
                ) {
                    val now = LocalDate.now()
                    val nowYear = now.year
                    val nowMonth = now.month.value
                    val nowDay = now.dayOfMonth

                    val existDailyCard =
                        nowYear == year && nowMonth == selectedMonth && nowDay == diaryMonthModels[selectedMonth - 1].count!!

//                Log.d("LaunchedEffect", "maxDateCardCount = $maxDateCardCount, itemsCount[selectedMonth]!! = ${itemsCount[selectedMonth]!!}")
                    // 그 월에 최대 갯수일 경우
                    if (maxDateCardCount == diaryMonthModels[selectedMonth - 1].count!! || existDailyCard) diaryMonthModels[selectedMonth - 1].count!!
                    else diaryMonthModels[selectedMonth - 1].count!! + 1
                }
//            Log.d("LaunchedEffect", "currentPage = ${pagerState.currentPage}, pageCount = ${pagerState.pageCount}")
//            Log.d("LaunchedEffect", "currentDay = $currentDay")

                // 커스텀 스냅 플링 동작 정의
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    // 스프링 애니메이션 스펙 사용
                    snapAnimationSpec = SpringSpec(
                        dampingRatio = 0.8f, stiffness = 400f
                    )
                )
            }

            LaunchedEffect(selectedMonth) {
//                Log.d("LaunchedEffect", "selectedMonth = $selectedMonth")
//                val pageCount = diaryMonthModels[selectedMonth - 1].count
//                itemsCount[selectedMonth] = pageCount
//                pagerState.requestScrollToPage(0)

                pagerState.animateScrollToPage(diaryMonthModels[selectedMonth - 1].count - 1)
                for (i in 0 until diaryMonthModels[selectedMonth - 1].count!!) {
                    cardStateMap[i] = CardState.IDLE
                    dragChargingMap[i] = 0f
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {


                // 일별 카드
                if (screenState == ScreenStep.DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_DETAIL || screenState == ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD || screenState == ScreenStep.FROM_DETAIL_TO_DATE_CARD) {
                    if (screenState == ScreenStep.DATE_CARD) GuideArrow(modifier)

                    HorizontalPager(state = pagerState,
                        verticalAlignment = Alignment.Top,
                        modifier = modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = dateFadeAnimatable.value // Apply fade animation
                            },
                        // 페이지 크기를 화면의 60%로 설정
                        pageSize = PageSize.Fixed(cardWidthDp),
                        // 중앙 정렬을 위한 패딩 적용
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        flingBehavior = flingBehavior,
                        key = {
                            it
                        }) { page ->

                        // 현재 페이지와의 거리에 따라 zIndex 계산 (0이 가장 앞, -1, -2... 순으로 뒤로 감)
                        val zIndex = -abs(pagerState.currentPage - page).toFloat()
                        val isCurrentPage = pagerState.currentPage == page

                        diaryMonthViewModel.updateCurrentDay(
                            pagerState.currentPage + 1
                        )

                        // 이 페이지가 드래그 중인지 여부
                        val isDragging =
                            cardStateMap[page] == CardState.DRAGGING || cardStateMap[page] == CardState.MAX_DRAGGED

                        // 드래그 값에 애니메이션 적용
                        // isDragging이 false일 때만 0으로 돌아가는 애니메이션 적용
                        val animationSpec: AnimationSpec<Float> = SpringSpec(
                            dampingRatio = 0.75f, stiffness = 300f
                        )

                        val flyAnimationSpec = tween<Float>(
                            durationMillis = 300, easing = LinearEasing
                        )

                        val animatedDragValue by animateFloatAsState(
                            targetValue = if (isDragging) dragChargingMap[page] ?: 0f else 0f,
                            animationSpec = animationSpec,
                            label = "dragAnimation"
                        )

                        val animatedFlyingValue by animateFloatAsState(
                            targetValue = if (cardStateMap[page] == CardState.FLYING) -screenHeightPx.toFloat() else 0f,
                            animationSpec = flyAnimationSpec,
                            finishedListener = { finalValue ->
                                // 날아가는 애니메이션이 완료되면 화면 상태를 DETAIL로 변경
                                if (finalValue <= -2000f && isCurrentPage) {
                                    //                        screenState = ScreenStep.DETAIL
                                    // COMPLETED 상태로 변경
                                    cardStateMap[page] = CardState.COMPLETED
                                }
                            },
                            label = "flyAnimation"
                        )

                        // 현재 드래그 값 (0f ~ 300f)을 정규화된 곡선 계수로 변환 (0f ~ 1f)
                        val dragProgress = animatedDragValue / 300f
                        if (isCurrentPage) visibility = 1f - dragProgress
//                        Log.d("Drag", "Drag progress: $dragProgress")
//                        Log.d(
//                            "SWIPE SCROLL",
//                            "page = ${page}, itemsCount[selectedMonth]!! = ${itemsCount[selectedMonth]!!}"
//                        )
                        if (page < diaryMonthModels[selectedMonth - 1].count!!) {
                            /// 일기 카드
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .zIndex(zIndex)
                                .pointerInput(page, isCurrentPage) {
                                    // 현재 페이지인 경우에만 드래그 감지
                                    if (isCurrentPage) {
                                        detectVerticalDragGestures(onDragStart = {
                                            // 드래그 시작 시 드래그 중 상태로 변경
                                            cardStateMap[page] = CardState.DRAGGING
                                            //                            isDraggingMap[page] = true
                                            dragChargingMap[page] = 0f
                                            Log.d("Drag", "Drag started for page $page")
                                        }, onDragEnd = {
                                            // 드래그 종료 시 드래그 중 상태 해제 (애니메이션이 시작됨)
                                            //                            if (cardStateMap[page] == CardState.MAX_DRAGGED) cardStateMap[page] =
                                            //                                CardState.FLYING
                                            //                            else cardStateMap[page] = CardState.IDLE
                                            val currentDrag = dragChargingMap[page]!!

                                            // 최대 드래그에 도달했는지 확인
                                            if (currentDrag >= 300f) {
                                                cardStateMap[page] = CardState.FLYING
                                                diaryMonthViewModel.updateScreenState(
                                                    ScreenStep.FROM_DATE_CARD_TO_DETAIL
                                                )
                                            } else if (currentDrag > 0f) {
                                                // 0보다 크고 300f 미만이면 DRAGGING 상태 유지
                                                cardStateMap[page] = CardState.DRAGGING
                                            } else {
                                                // 0이면 IDLE 상태로 복귀
                                                cardStateMap[page] = CardState.IDLE
                                                dragChargingMap[page] = 0f
                                            }
                                            //                            isDraggingMap[page] = false
                                            dragChargingMap[page] = 0f
                                            Log.d("Drag", "Drag ended, animation will start")
                                        }, onVerticalDrag = { change, dragAmount ->
                                            // 현재 값에 드래그 양 추가하고 맵 업데이트
                                            val currentDrag = dragChargingMap[page] ?: 0f
                                            val newDrag =
                                                (currentDrag + dragAmount).coerceIn(0f, 300f)
                                            dragChargingMap[page] = newDrag

                                            if (newDrag == 300f) {
                                                cardStateMap[page] = CardState.MAX_DRAGGED
                                            } else {
                                                cardStateMap[page] = CardState.DRAGGING
                                            }

                                            //                            Log.d(
                                            //                                "Drag", "Dragging page $page: $newDrag, progress: ${newDrag / 300f}"
                                            //                            )
                                        })
                                    }
                                }
                                .graphicsLayer {
                                    // 현재 페이지에서의 상대적 위치 계산
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                                    //                    Log.d(
                                    //                        "Pageer",
                                    //                        "[page ${page}] currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}"
                                    //                    )


                                    // Y축 변환에 점진적인 변화 적용
                                    val baseTranslationY =
                                        (screenSizeDp.second.value * 0.5f) * pageOffset.absoluteValue.coerceIn(
                                            0f, 1f
                                        )

                                    // 현재 페이지인 경우에만 애니메이션된 드래그 값 적용
                                    val dragY = if (isCurrentPage) {
                                        if (cardStateMap[page] == CardState.FLYING) {
                                            animatedFlyingValue
                                        } else {
                                            animatedDragValue
                                        }
                                    } else {
                                        0f
                                    }


                                    if (screenState == ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD) {
                                        if (!isCurrentPage) {
                                            translationX =
                                                dailyInitialXAnimatable.value * pageOffset.coerceIn(
                                                    -1f, 1f
                                                )
                                            translationY = dailyInitialYAnimatable.value
                                        }
                                        //                        if (isCurrentPage) alpha = 0f

                                        rotationZ =
                                            dailyRotateZAnimatable.value * pageOffset.coerceIn(
                                                -1f, 1f
                                            )
                                    } else {
                                        // 페이지별 Y 위치 조정
                                        if (isCurrentPage) {
                                            translationY =
                                                baseTranslationY + dragY + dateTranslationYAnimatable.value
                                        } else {
                                            translationY =
                                                baseTranslationY + dateTranslationYAnimatable.value // Apply transitionY animation
                                        }
                                        // 중앙 카드가 아닌 경우 왼쪽 또는 오른쪽으로 너비 48% 움직임 조정
                                        if (!isCurrentPage) {
                                            translationX =
                                                (screenSizeDp.first.value * 0.48f) * pageOffset.coerceIn(
                                                    -1f, 1f
                                                )
                                        }
                                        // 중앙에서 멀어질수록 약간 흐려지는 효과
                                        alpha = 0.6f + (1f - pageOffset.absoluteValue.coerceIn(
                                            0f, 1f
                                        )) * 0.4f
                                        // Z축 회전 - 왼쪽 카드는 -13도, 오른쪽 카드는 13도, 현재 카드는 0도
                                        rotationZ = -13f * pageOffset.coerceIn(-1f, 1f)
                                    }


                                    //                    // 로그 출력으로 값 확인
                                    //                    if (isCurrentPage) {
                                    //                        Log.d(
                                    //                            "GraphicsLayer", "Page $page: dragY=$dragY, progress=$dragProgress"
                                    //                        )
                                    //                    }


                                }, contentAlignment = Alignment.Center
                            ) {
//                                Log.d("SWIPE SCROLL", "currentDay = $page")
//                                Log.d("SWIPE SCROLL", "month = ${selectedMonth - 1} day Size = ${dateDiaries[year]!![selectedMonth - 1].size}")

                                val diaryModel = dateDiaries[year]!![selectedMonth - 1][page]
                                when (diaryModel) {
                                    is DiaryModel -> {
                                        SwipeDiaryCard(
                                            diaryModel = diaryModel
//                                    image = ImageBitmap.imageResource(
//                                        context.resources, R.drawable.cat2
//                                    )
                                            ,
                                            dragProgress = dragProgress, // 드래그 진행 정도 전달 (0.0 ~ 1.0)
                                            maxDragged = cardStateMap[page] == CardState.MAX_DRAGGED// 최대 드래그 여부 전달
                                        )
                                    }

                                    is EmptyDiaryModel -> {
                                        Box(
                                            modifier = modifier
                                                .fillMaxHeight()
                                                .background(color = Color.Red)
                                        )
                                    }
                                }

                            }
                        } else {
                            /// 일기 추가 카드
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(zIndex)
                                    .graphicsLayer {
                                        // 현재 페이지에서의 상대적 위치 계산
                                        val pageOffset =
                                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                                        //                    Log.d(
                                        //                        "Pageer",
                                        //                        "[page ${page}] currentPageOffsetFraction: ${pagerState.currentPageOffsetFraction}"
                                        //                    )
                                        // Y축 변환에 점진적인 변화 적용
                                        val baseTranslationY =
                                            (screenSizeDp.second.value * 0.5f) * pageOffset.absoluteValue.coerceIn(
                                                0f, 1f
                                            )

                                        // 현재 페이지인 경우에만 애니메이션된 드래그 값 적용
                                        val dragY = if (isCurrentPage) {
                                            if (cardStateMap[page] == CardState.FLYING) {
                                                animatedFlyingValue
                                            } else {
                                                animatedDragValue
                                            }
                                        } else {
                                            0f
                                        }


                                        if (screenState == ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD) {
                                            if (!isCurrentPage) {
                                                translationX =
                                                    dailyInitialXAnimatable.value * pageOffset.coerceIn(
                                                        -1f, 1f
                                                    )
                                                translationY = dailyInitialYAnimatable.value
                                            }
                                            //                        if (isCurrentPage) alpha = 0f

                                            rotationZ =
                                                dailyRotateZAnimatable.value * pageOffset.coerceIn(
                                                    -1f, 1f
                                                )
                                        } else {
                                            // 페이지별 Y 위치 조정
                                            if (isCurrentPage) {
                                                translationY =
                                                    baseTranslationY + dragY + dateTranslationYAnimatable.value
                                            } else {
                                                translationY =
                                                    baseTranslationY + dateTranslationYAnimatable.value // Apply transitionY animation
                                            }
                                            // 중앙 카드가 아닌 경우 왼쪽 또는 오른쪽으로 너비 48% 움직임 조정
                                            if (!isCurrentPage) {
                                                translationX =
                                                    (screenSizeDp.first.value * 0.48f) * pageOffset.coerceIn(
                                                        -1f, 1f
                                                    )
                                            }
                                            // 중앙에서 멀어질수록 약간 흐려지는 효과
                                            alpha = 0.6f + (1f - pageOffset.absoluteValue.coerceIn(
                                                0f, 1f
                                            )) * 0.4f
                                            // Z축 회전 - 왼쪽 카드는 -13도, 오른쪽 카드는 13도, 현재 카드는 0도
                                            rotationZ = -13f * pageOffset.coerceIn(-1f, 1f)
                                        }


                                        //                    // 로그 출력으로 값 확인
                                        //                    if (isCurrentPage) {
                                        //                        Log.d(
                                        //                            "GraphicsLayer", "Page $page: dragY=$dragY, progress=$dragProgress"
                                        //                        )
                                        //                    }


                                    }, contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(dailyCardSize.height)
                                        .width(dailyCardSize.width)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            BorderStroke(6.dp, WhiteTextColor),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            onNavigateForm()
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center),
                                        imageVector = Icons.Rounded.Add,
                                        tint = WhiteTextColor,
                                        contentDescription = null
                                    )
                                }


                            }
                        }
                    }

                }

                // DETAIL 화면
                if (screenState == ScreenStep.DETAIL || screenState == ScreenStep.FROM_DETAIL_TO_DATE_CARD) {

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val flipDateCardModel =
                            dateDiaries[year]!![selectedMonth - 1][currentDay - 1] as DiaryModel

                        when (flipDateCardModel.weather) {
                            Weather.RAIN -> RainBackgroundEffect()
                            Weather.SNOW -> SnowBackgroundEffect()
                            Weather.DEFAULT ->
                                DefaultBackgroundEffect()
                        }

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.graphicsLayer {
                            translationX = ((screenWidthPx - flipCardWidthPx) / 2)
                            // 초기에는 화면 위에서 시작
                            translationY = slidingYOffset
                            // 애니메이션되는 알파 값
                            alpha = cardAlpha
                            // 약간 커지는 스케일 효과
                            scaleX = cardScale
                            scaleY = cardScale
                        }) {

                            FlippableDiaryCard(
                                diaryModel = flipDateCardModel,
                            )

                        }
                        Row(
                            modifier
                                .fillMaxWidth()
                                .offset(y = screenSizeDp.second - 50.dp)
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Row(modifier.noRippleClickable {
                                val date = "$year-${
                                    selectedMonth.toString().padStart(2, '0')
                                }-${currentDay.toString().padStart(2, '0')}"
                                onNavigatePayment(date)
                            }) {
                                Text("자세히 보기", style = Heading_M_SemiBold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = WhiteTextColor,
                                )
                            }

                        }
                    }
                }


                // 일별 카드
                if (screenState == ScreenStep.DATE_CARD || screenState == ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD || screenState == ScreenStep.FROM_DETAIL_TO_DATE_CARD) {
                    Column(
                        modifier
                            .zIndex(-100f)
                            .graphicsLayer {
                                alpha = dateFadeAnimatable.value // Use the animatable value
                            }) {
                        Text(
                            "작성한 일기를 찾아보세요",
                            style = Heading_L_Bold.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 60.dp)
                        )
                        Spacer(modifier = Modifier.height(100.dp))
                        Text(
                            "${selectedMonth}월 ${currentDay}일",
                            style = Display_L_Bold.copy(textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                // 월별 카드
                if (screenState == ScreenStep.MONTH_CARD || screenState == ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD || screenState == ScreenStep.FROM_DATE_CARD_TO_MONTH_CARD) {
                    val items = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
                    val contentPadding = PaddingValues(24.dp)
                    Column {
                        DefaultAppBar("일기카드", navigateUp = {
                            scope.launch {
                                drawerState.open()
                            }
                        }, modifier = modifier.graphicsLayer {
                            alpha = monthFadeAnimatable.value
                        })
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .graphicsLayer {
                                    alpha = monthFadeAnimatable.value
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = {
                                diaryMonthViewModel.updateYear(year - 1)
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.ChevronLeft,
                                    contentDescription = null,
                                    tint = WhiteTextColor,
                                )
                            }
                            Text(
                                year.toString(),
                                style = Heading_L_Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            IconButton(onClick = {
                                diaryMonthViewModel.updateYear(year + 1)
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = WhiteTextColor,
                                )
                            }
                        }
                        LazyVerticalGrid(
                            modifier = Modifier.weight(1f),
                            state = scrollState,
                            columns = GridCells.Fixed(2),
                            contentPadding = contentPadding,
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(36.dp),
                        ) {
                            // LazyVerticalGrid 내부 items 루프에서 사용할 부분
                            items(items = items, key = { it }) { month ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = modifier.graphicsLayer {
                                        alpha =
                                            if (month == selectedMonth) 1f else monthFadeAnimatable.value
                                    }) {
                                    Text("$month 월",
                                        style = Heading_M_SemiBold,
                                        modifier = modifier.graphicsLayer {
                                            alpha = monthFadeAnimatable.value
                                        })
                                    Spacer(Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                            // 각 아이템의 화면 상 위치 저장
                                            itemPositions[month] = coordinates.positionOnScreen()
                                        }, contentAlignment = Alignment.Center
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                                .data(monthCards[month - 1]).crossfade(true)
                                                .decoderFactory(SvgDecoder.Factory())  // SVG 디코더 추가
                                                .build(),
                                                alpha = if (diaryMonthModels[month - 1].count == 0) .2f else 1f,
                                                contentDescription = "Month Card",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .width(monthlyCardSize.width)
                                                    .height(monthlyCardSize.height)
                                                    .pointerInput(month) {
                                                        detectTapGestures { offset ->
                                                            /// 일기 카드 없을 시 무시
                                                            if (diaryMonthModels[month - 1].count == 0) return@detectTapGestures


                                                            // 탭된 아이템 정보 저장
                                                            val pageCount =
                                                                diaryMonthModels[month - 1].count
                                                            diaryMonthViewModel.updateCurrentDay(
                                                                pageCount + 1
                                                            )
                                                            diaryMonthViewModel.updateMonth(month)
                                                            Log.d(
                                                                "Click",
                                                                "month = $month, currentDay = $currentDay "
                                                            )


                                                            // 현재 위치에서 화면 중앙까지의 오프셋 계산
                                                            val currentPosition =
                                                                itemPositions[month] ?: Offset.Zero

                                                            val dailyCardOffsetY =
                                                                (screenHeightPx - monthlyCardSize.height.value * density.density) / 2
                                                            val dailyCardOffsetX =
                                                                (screenWidthPx - monthlyCardSize.width.value * density.density) / 2
                                                            val transitionY =
                                                                dailyCardOffsetY - currentPosition.y + fromWindowHeight.value * density.density// + (상태 바 높이)
                                                            val transitionX =
                                                                dailyCardOffsetX - currentPosition.x

                                                            Log.d(
                                                                "Click", "TransitionY $transitionY"
                                                            )
                                                            diaryMonthViewModel.updateScreenState(
                                                                ScreenStep.FROM_MONTH_CARD_TO_DATE_CARD
                                                            )

                                                            scope.launch {
                                                                coroutineScope {
                                                                    // 여러 애니메이션을 병렬로 실행
                                                                    launch {
                                                                        monthTranslationYAnimatable.animateTo(
                                                                            targetValue = transitionY,
                                                                            animationSpec = tween(
                                                                                300,
                                                                                easing = LinearEasing
                                                                            )
                                                                        )
                                                                    }

                                                                    launch {
                                                                        monthTranslationXAnimatable.animateTo(
                                                                            targetValue = transitionX,
                                                                            animationSpec = tween(
                                                                                300,
                                                                                easing = LinearEasing
                                                                            )
                                                                        )
                                                                    }

                                                                    launch {
                                                                        monthScaleAnimatable.animateTo(
                                                                            targetValue = 1.54f,
                                                                            animationSpec = tween(
                                                                                300,
                                                                                easing = LinearEasing
                                                                            )
                                                                        )
                                                                    }
                                                                }

                                                                // 모든 애니메이션이 완료된 후 실행될 코드
                                                                // 상태 변경은 여기에 배치할 수 있습니다
                                                            }

                                                            // 애니메이션과 독립적으로 즉시 실행하려면 애니메이션 코드 밖에 배치

                                                        }
                                                    }
                                                    .graphicsLayer {
                                                        if (selectedMonth == month) {
                                                            scaleX = monthScaleAnimatable.value
                                                            scaleY = monthScaleAnimatable.value
                                                            translationY =
                                                                monthTranslationYAnimatable.value //+ index * 10f
                                                            translationX =
                                                                monthTranslationXAnimatable.value// + index * 10f
                                                            alpha = monthCardFadeAnimatable.value
                                                        }
                                                        //                                            translationX =
                                                        //                                            translationX =
                                                        //                                                 monthTranslationXAnimatable.value // i * -20f +
                                                        //
                                                        //                                            translationY = monthTranslationYAnimatable.value


                                                        //                                            alpha =
                                                        //                                                if (i == monthCards.size - 1) 1f else monthFadeAnimatable.value
                                                    }
                                                    .clip(RoundedCornerShape(16.dp)))
                                        }


                                    }
                                }
                            }
                        }
                    }


                }

            }
        }

    }


}


@Composable
fun GuideArrow(modifier: Modifier = Modifier) {
    // 1. 메모이제이션을 사용하여 컴포지션 재사용
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.down_arrow)
    )

    // 2. 애니메이션 상태를 기억하여 재사용
    val animationState = remember {
        LottieAnimationState(
            isPlaying = true, speed = 1f, iterations = LottieConstants.IterateForever
        )
    }

    val screenSizeDp = ScreenSizeUtil.getScreenSizeDp()
    val guideHeightDp = screenSizeDp.second * 0.3f
    val offsetY = (screenSizeDp.second.value - guideHeightDp.value * 1.2)

    // 3. 렌더링 로직 최적화
    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .height(guideHeightDp)
            .zIndex(10f),
        contentAlignment = Alignment.BottomCenter
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

// 애니메이션 상태를 캡슐화하는 클래스
data class LottieAnimationState(
    val isPlaying: Boolean,
    val speed: Float,
    val iterations: Int,
)

@Composable
fun SwipeDiaryCard(
    diaryModel: DiaryModel,
    dragProgress: Float = 0f, // 드래그 진행도 (0.0 ~ 1.0)
    maxDragged: Boolean = false, // 최대 드래그 여부
) {
    // 화면 너비 가져오기
    val screenSizeDp = ScreenSizeUtil.getScreenSizeDp()

    // 카드 너비를 화면의 60%에 맞게 설정 (패딩 고려)
    val dailyCardSize = getDailyCardSize(screenSizeDp.first.value, screenSizeDp.second.value)


    // 드래그 진행도에 따른 곡선 효과 애니메이션
    // 직접 dragProgress를 사용하거나, maxDragged가 true일 때 추가 애니메이션 적용
    val animationProgress by animateFloatAsState(
        targetValue = if (maxDragged) 1f else dragProgress,
        animationSpec = tween(if (maxDragged) 200 else 50), // 최대 드래그 시 더 긴 애니메이션
        label = "curveAnimation"
    )

    // 최대 드래그 시 떨림 애니메이션 추가
    val shakeAmount = remember { Animatable(0f) }

    // 이미지 비트맵 상태
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }


    val context = LocalContext.current
    val imageSrc = diaryModel.imgSrc

    val scope = rememberCoroutineScope()
    // 이미지 로딩
    LaunchedEffect(imageSrc) {
        scope.launch {
            try {
                // Coil을 사용하여 이미지 로드
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(imageSrc).build()

                val result = imageLoader.execute(request)

                when (result) {
                    is SuccessResult -> {
                        // SuccessResult에서 비트맵 가져오기
                        imageBitmap = result.image.toBitmap().asImageBitmap()
                    }

                    else -> {
                    }
                }
            } catch (e: Exception) {
                Log.e("SwipeDiaryCard", "이미지 로드 실패", e)
            } finally {
            }
        }
    }

    // 최대 드래그 상태가 변경될 때 진동 애니메이션 시작/중지
    LaunchedEffect(maxDragged) {
        if (maxDragged) {
            // 최대 드래그 시 진동 애니메이션 시작
            // 무한 반복 루프 생성
            while (true) {
                // -3부터 3까지 빠르게 진동
                shakeAmount.animateTo(
                    targetValue = 3f, animationSpec = tween(80, easing = LinearEasing)
                )
                shakeAmount.animateTo(
                    targetValue = -3f, animationSpec = tween(80, easing = LinearEasing)
                )

                // 약간의 불규칙성 추가 (더 자연스러운 효과)
                shakeAmount.animateTo(
                    targetValue = 2f, animationSpec = tween(60, easing = LinearEasing)
                )
                shakeAmount.animateTo(
                    targetValue = -2f, animationSpec = tween(60, easing = LinearEasing)
                )
            }
        } else {
            // 최대 드래그가 아닐 때 진동 멈춤
            shakeAmount.animateTo(0f, animationSpec = tween(100))
        }
    }

    // 곡선 효과와 높이 계산 - 드래그 진행도에 따라 동적으로 변화
    val curveAmount = animationProgress * 40f

    // 높이 축소량 계산 - 윗부분이 줄어들도록
    val heightReduction = (curveAmount.dp * 2f)
    val reducedHeight = dailyCardSize.height - heightReduction

    // Box를 사용하여 Canvas를 아래쪽에 정렬 (떨림 효과 적용)
    Box(
        modifier = Modifier
            .size(dailyCardSize.width, dailyCardSize.height)
            .offset(x = shakeAmount.value.dp) // 수평 진동 효과
            .graphicsLayer {
                // 최대 드래그 시 약간의 스케일 진동 추가
                if (maxDragged) {
                    val scaleVibration = 1f + (sin(System.currentTimeMillis() / 50f) * 0.01f)
                    scaleX = scaleVibration
                    scaleY = scaleVibration
                }
            },
//        contentAlignment = Alignment.BottomCenter // 아래쪽 정렬로 변경
    ) {
        Canvas(
            modifier = Modifier
                .size(dailyCardSize.width, reducedHeight)
                .align(Alignment.BottomCenter) // 하단 정렬로 변경
        ) {
            val w = size.width
            val h = size.height
            val cornerRadius = 20.dp.toPx()

            val curve = curveAmount // 드래그에 따라 변하는 곡선 정도

            val path = Path().apply {
                moveTo(curve + cornerRadius, 0f)
                lineTo(w - curve - cornerRadius, 0f)
                quadraticTo(w - curve, 0f, w - curve, cornerRadius)
                cubicTo(w, h * 0.25f, w, h * 0.75f, w - curve, h - cornerRadius)
                quadraticTo(w - curve, h, w - curve - cornerRadius, h)
                lineTo(curve + cornerRadius, h)
                quadraticTo(curve, h, curve, h - cornerRadius)
                cubicTo(0f, h * 0.75f, 0f, h * 0.25f, curve, cornerRadius)
                quadraticTo(curve, 0f, curve + cornerRadius, 0f)
                close()
            }

            clipPath(path) {
                // 줄어든 높이에 맞춰 이미지 표시
                imageBitmap?.let {
                    drawImage(
                        image = it, dstSize = IntSize(w.toInt(), h.toInt())
                    )
                }
            }

            // 드래그 진행도에 따라 그라데이션 강도 변화
            val gradientAlpha = 0.25f + (animationProgress * 0.15f)

            drawPath(
                path = path, brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = gradientAlpha),
                        Color.Black.copy(alpha = gradientAlpha)
                    ), center = Offset(w / 2, h / 2), radius = w
                ), style = Fill
            )

            // 드래그 진행도에 따라 두께 변화
            val strokeWidth = (animationProgress * 5f)

            // 최대 드래그 시 외곽선 깜빡임 효과
            val pulseEffect = if (maxDragged) {
                // sin 함수를 사용해 0.5와 1.0 사이를 부드럽게 오가는 펄스 효과
                0.5f + (sin(System.currentTimeMillis() / 150f) + 1f) / 4f
            } else {
                1f
            }

            drawPath(
                path = path, color = if (animationProgress == 0f) Color.Transparent
                else Color.Red.copy(alpha = pulseEffect), style = Stroke(width = strokeWidth)
            )
        }
    }
}


