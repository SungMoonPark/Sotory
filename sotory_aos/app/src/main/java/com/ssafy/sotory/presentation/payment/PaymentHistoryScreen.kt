package com.ssafy.sotory.presentation.payment

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.sotory.R
import com.ssafy.sotory.common.SharedToastViewModel
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.common.presentation.ui.OptionModalBottomSheet
import com.ssafy.sotory.common.presentation.ui.button.OptionModalButton
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.presentation.diary.LottieAnimationState
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryHistoryFormViewModel
import com.ssafy.sotory.presentation.payment.ui.PaymentCard
import com.ssafy.sotory.presentation.payment.viewmodel.NavigationAction
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentEvent
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentHistoryUiState
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentHistoryViewModel
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    onNavigate: (NavigationAction) -> Unit,
    viewModel: PaymentHistoryViewModel = hiltViewModel(),
    toastViewModel: SharedToastViewModel,
    drawerState: DrawerState,
    onNavigateCheck: (String) -> Unit,
) {


    val context = LocalContext.current
    var isShowModal: Boolean by remember { mutableStateOf(false) }
    var isShowPaymentEdit: Boolean by remember { mutableStateOf(false) }
    var isShowDiaryEdit: Boolean by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedPayment by remember { mutableStateOf<PaymentModel?>(null) }

    val paymentHistoriesUiState by viewModel.paymentHistoriesUiState.collectAsStateWithLifecycle()
    val enabledAddButton by viewModel.enabledAddButton.collectAsStateWithLifecycle()
    val isCreated by viewModel.isCreated.collectAsStateWithLifecycle()
    Log.d("testModel", "testModel ${paymentHistoriesUiState}")
    when (paymentHistoriesUiState) {
        is PaymentHistoryUiState.Error -> {
            val state = paymentHistoriesUiState as PaymentHistoryUiState.Error
            Log.d("testModel", "testModel ${state.message}")
        }

        else -> {}
    }
    // 토스트 이벤트 수신
    LaunchedEffect(Unit) {
        toastViewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

//    val models by viewModel.paymentHistories.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { action ->
            when (action) {
                is NavigationAction.ToPaymentAdd -> {
                    onNavigate(action)
                }

                is NavigationAction.ToDiaryEdit -> {
                    onNavigate(action)
                }

                is NavigationAction.ToPaymentEdit -> {
                    Log.d("ToPaymentEdit", "ToPaymentEdit ${action.payment}")
                    onNavigate(action)
                }
            }

        }
    }

    Scaffold(modifier = Modifier
        .background(BackgroundColor)
        .padding(), topBar = {
        DefaultAppBar("흔적 작성", actions = {
            Row {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            viewModel.onEvent(event = PaymentEvent.AddClicked)
                        },
                    tint = WhiteTextColor,
                )
            }
        }, navigateUp = {
            scope.launch {
                drawerState.open()
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "하루의 흔적",
                style = Heading_L_Bold,
                color = WhiteTextColor,
            )
//            CardStackScreen()

            Spacer(modifier = Modifier.height(32.dp))
            when (paymentHistoriesUiState) {
                is PaymentHistoryUiState.Success -> {
                    val models = (paymentHistoriesUiState as PaymentHistoryUiState.Success).payments
                    if (models.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "아직 작성한 흔적이 없습니다.",
                                style = Heading_L_Bold,
                                color = WhiteTextColor
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(25.dp)
                        ) {
                            items(models) {
                                val diaryFormViewModel =
                                    if (it.diary == null) hiltViewModel<DiaryHistoryFormViewModel, DiaryHistoryFormViewModel.DiaryHistoryViewModelFactory>(
                                        key = it.paymentDiaryId
                                    ) { factory ->
                                        factory.create(it.paymentDiaryId)
                                    } else null
                                PaymentCard(it, onOptionClick = {
                                    selectedPayment = it
                                    isShowPaymentEdit = it.isUserAdded
                                    isShowDiaryEdit = it.diary != null
                                    isShowModal = true
                                }, viewModel = diaryFormViewModel)
                            }
                        }
                        if (isCreated) Text(
                            "오늘 일기를 생성하셨어요!",
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .height(52.dp)
                                .fillMaxWidth(),
                            style = Heading_M_SemiBold
                        )
                        else DefaultTextButton(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .height(52.dp)
                                .fillMaxWidth(), content = "일기 생성하기", onClick = {
                                val now = LocalDate.now()
                                val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                onNavigateCheck(date)
                            }, enabled = enabledAddButton
                        )
                    }


                    if (isShowModal) {
                        OptionModalBottomSheet(content = {
                            Column {
                                if (isShowPaymentEdit) {
                                    OptionModalButton(onClick = {

                                        viewModel.onEvent(
                                            event = PaymentEvent.EditPaymentClicked(
                                                payment = selectedPayment!!
                                            )
                                        )
                                        dismissModalBottomSheet(scope, sheetState) {
                                            isShowModal = false
                                            selectedPayment = null
                                        }
                                    }, content = "내역 수정하기")
                                    OptionModalButton(onClick = {

                                        viewModel.onEvent(
                                            event = PaymentEvent.DeletePaymentClicked(
                                                paymentId = selectedPayment!!.paymentId
                                            )
                                        )
                                        dismissModalBottomSheet(scope, sheetState) {
                                            isShowModal = false
                                            selectedPayment = null
                                        }
                                    }, content = "내역 삭제하기")
                                }
                                if (isShowDiaryEdit) OptionModalButton(onClick = {
                                    if (selectedPayment != null) {
                                        viewModel.onEvent(
                                            event = PaymentEvent.EditDiaryClicked(
                                                payment = selectedPayment!!
                                            )
                                        )
                                        dismissModalBottomSheet(scope, sheetState) {
                                            isShowModal = false
                                            selectedPayment = null
                                        }
                                    } else {
                                        Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }, content = "흔적 수정하기")
                                if (isShowDiaryEdit) OptionModalButton(onClick = {
                                    selectedPayment?.let {
                                        viewModel.onEvent(
                                            event = PaymentEvent.DeleteDiaryClicked(
                                                paymentDiaryId = it.paymentDiaryId
                                            )
                                        )
                                    }
                                    dismissModalBottomSheet(scope, sheetState) {
                                        isShowModal = false
                                        selectedPayment = null
                                    }
                                }, content = "흔적 삭제하기")
                            }
                        }, sheetState = sheetState, onDismissRequest = {
                            isShowPaymentEdit = false
                            selectedPayment = null
                            isShowDiaryEdit = false
                            isShowModal = false
                        })
                    }
                }

                is PaymentHistoryUiState.Error -> {
                    Text("에러")
                }

                is PaymentHistoryUiState.Loading -> {
                    CircularProgressIndicator()
                }
            }


        }

    }
}

@ExperimentalMaterial3Api
fun dismissModalBottomSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    callback: () -> Unit,
) {
    scope.launch {
        sheetState.hide()
    }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            callback()
        }
    }
}

