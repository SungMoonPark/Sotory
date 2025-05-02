package com.ssafy.sotory.presentation.diary

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomGradientButton
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryGenerationViewModel
import com.ssafy.sotory.presentation.diary.viewmodel.NavigationEvent
import com.ssafy.sotory.presentation.payment.ui.PaymentCardList
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentFetchViewModel
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentReadUiState
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun DiaryCheckScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onNavigateWrite: () -> Unit,
    onNavigateWaiting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentFetchViewModel = hiltViewModel(),
    diaryGenerationViewModel: DiaryGenerationViewModel = hiltViewModel(),
    date: String,
) {

    val paymentReadUiState by viewModel.paymentReadUiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // NavigationEvent 수집
    LaunchedEffect(Unit) {
        diaryGenerationViewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToLoadingScreen -> {
                    onNavigateWaiting()
                }
                // 다른 이벤트는 각각의 화면에서 처리
                is NavigationEvent.ToCompletionScreen -> {


                }

                is NavigationEvent.ToErrorScreen -> {

                }
            }
        }
    }
    Scaffold(topBar = {
        DefaultAppBar(
            title = "흔적 생성",
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp
        )
    }) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            when (paymentReadUiState) {
                is PaymentReadUiState.Success -> {
                    val payments = (paymentReadUiState as PaymentReadUiState.Success).payments
                    Log.d("PaymentListScreen", "payments: ${payments.size}")
                    val existDiaryPayments = payments.filter {
                        it.diary != null
                    }
                    Column(
                        modifier.fillMaxSize(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            "오늘의 흔적 ${existDiaryPayments.size}개로 일기를 생성하시겠습니까?",
                            style = Heading_M_SemiBold
                        )
                        Spacer(modifier = modifier.height(25.dp))
                        Column(
                            modifier.weight(1f),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        ) {
                            PaymentCardList(
                                paymentModels = existDiaryPayments,
                            )

                        }
                        Spacer(modifier = modifier.height(25.dp))
//                        Text(
//                            text = "더 작성하기",
//                            style = Heading_S_SemiBold.copy(
//                                textDecoration = TextDecoration.Underline,
//                                color = WhiteTextColor,
//                            ),
//                            modifier = Modifier.noRippleClickable {
//                                onNavigateWrite()
//                            },
//                        )
                        Spacer(modifier = modifier.height(25.dp))
                        BottomGradientButton(content = "생성하기", onClick = {
                            diaryGenerationViewModel.generateDiary()
                        })

                    }


                }

                is PaymentReadUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is PaymentReadUiState.Error -> {
                    Text("error")
                }
            }

        }

    }


}
