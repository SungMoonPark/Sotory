package com.ssafy.sotory.presentation.diary

import CardSizeUtil
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryGenerationViewModel
import com.ssafy.sotory.presentation.diary.viewmodel.NavigationEvent
import com.ssafy.sotory.presentation.diary.viewmodel.OtherDiaryUiState
import com.ssafy.sotory.presentation.diary.viewmodel.OtherDiaryViewModel
import com.ssafy.sotory.util.ScreenSizeUtil
import kotlin.math.absoluteValue

@Composable
fun DiaryOtherCardScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryGenerationViewModel,
    otherDiaryViewModel: OtherDiaryViewModel = hiltViewModel(),
    onNavigateComplete: (diary: DiaryModel) -> Unit,
) {
    val list = remember {
        mutableListOf(10)
    }

    val generationState by viewModel.generationState.collectAsStateWithLifecycle()
    val otherDiaryState by otherDiaryViewModel.otherDiaryUiState.collectAsStateWithLifecycle()

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
    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {

            when (otherDiaryState) {
                is OtherDiaryUiState.Error -> {

                }

                is OtherDiaryUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is OtherDiaryUiState.Success -> {
                    val diaries = (otherDiaryState as OtherDiaryUiState.Success).diaries
                    ImageCarousel(
                        imageUrls = diaries.imgSrc
                    )

//                    LazySwipeCards {
//                        // Add items
//                        items(list) { it ->
//                            OtherCard(
//                                it.toString(),
//                                modifier
//                                    .height(
//                                        300.dp
//                                    )
//                                    .width(200.dp)
//                            )
//                        }
//                    }

//                    val diaries = (otherDiaryState as OtherDiaryUiState.Success).diaries
//                    Log.d(  "CardStack", "build!!")
//                    CardStack(
//                        imageUrls = diaries.imgSrc//.toMutableList()
//                    )
                }
            }

        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    modifier: Modifier = Modifier, imageUrls: MutableList<String>
) {
    val pagerState = rememberPagerState { imageUrls.size }
    val screenSize = ScreenSizeUtil.getScreenSizeDp()
    val cardSize = CardSizeUtil.getDetailCardSize(screenSize.first.value)
    Column(
        modifier
            .defaultMinSize(minHeight = 300.dp)
            .fillMaxWidth()
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 10.dp,
            contentPadding = PaddingValues(horizontal = 30.dp)
        ) { page ->
            AsyncImage(
                model = imageUrls[page], contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardSize.height)
                    .graphicsLayer {
                        val pageOffset =
                            (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue

                        lerp(
                            start = 75.dp,
                            stop = 100.dp,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleY = scale / 100.dp
                        }
                    }
                    .clip(RoundedCornerShape(16.dp)),

                )
//            Image(
//                painter = painterResource(id = imageUrls[page]),
//                contentDescription = "",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .graphicsLayer {
//                        val pageOffset =
//                            (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue
//
//                        lerp(
//                            start = 75.dp,
//                            stop = 100.dp,
//                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                        ).also { scale ->
//                            scaleY = scale / 100.dp
//                        }
//                    },
//                contentScale = ContentScale.Crop
//            )
        }

    }

}