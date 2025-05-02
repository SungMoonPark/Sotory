package com.ssafy.sotory.presentation.payment

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.sotory.common.SharedToastViewModel
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.common.presentation.ui.OptionModalBottomSheet
import com.ssafy.sotory.common.presentation.ui.button.BottomGradientButton
import com.ssafy.sotory.common.presentation.ui.textfield.CurrencyVisualTransformation
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultOutlinedTextField
import com.ssafy.sotory.data.dto.payment.Category
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentAddViewModel
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentFormUiState
import com.ssafy.sotory.ui.theme.Black200
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.BottomModalSheetBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.util.ScreenSizeUtil
import com.ssafy.sotory.util.formatDateTime
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    viewModel: PaymentAddViewModel,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    paymentModel: PaymentModel?,
    toastViewModel: SharedToastViewModel,
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.formUiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = now.format(formatter)
        viewModel.updateTransactionTime(formatted)

        Log.d("PaymentFormScreen", "PaymentFormScreen: $paymentModel")
        paymentModel?.let {
            val merchantNameValue = TextFieldValue(
                text = it.merchantName, selection = TextRange(it.merchantName.length)
            )
            val transactionBalanceValue = TextFieldValue(
                text = it.transactionBalance, selection = TextRange(it.transactionBalance.length)
            )
            viewModel.updateMerchantName(merchantNameValue)
            Category.findByName(it.categoryName)?.let { category ->
                viewModel.updateCategoryName(category)
            }
            viewModel.updateTransactionTime(it.transactionTime)
            viewModel.updateTransactionBalance(transactionBalanceValue)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is PaymentFormUiState.SubmissionSuccess -> {               // 토스트 메시지 트리거
                if (paymentModel == null) {
                    toastViewModel.showToast("결제 내역이 성공적으로 추가되었습니다")
                } else {
                    toastViewModel.showToast("결제 내역이 성공적으로 수정되었습니다")
                }
                navigateUp()
            }

            is PaymentFormUiState.SubmissionError -> {

            }

            else -> {}
        }
    }


    val appbarTitle = if (paymentModel != null) "내역 수정" else "내역 추가"
    val buttonContent = if (paymentModel != null) "내역 수정하기" else "내역 추가하기"
    val scope = rememberCoroutineScope()
    var isTimeShowModal: Boolean by remember { mutableStateOf(false) }
    var isCategoryShowModal: Boolean by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val merchantName by viewModel.merchantName.collectAsStateWithLifecycle()
    val category by viewModel.categoryName.collectAsStateWithLifecycle()
    val transactionTime by viewModel.transactionTime.collectAsStateWithLifecycle()
    val transactionBalance by viewModel.transactionBalance.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()


    val scrollState = rememberScrollState()


    val formatTime = formatDateTime(transactionTime)

    Scaffold(topBar = {
        DefaultAppBar(
            title = appbarTitle, canNavigateBack = canNavigateBack, navigateUp = navigateUp
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()

        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                DefaultOutlinedTextField(
                    label = "장소",
                    onValueChange = { viewModel.updateMerchantName(it) },
                    value = merchantName,
                    singleLine = true,
                    placeholder = "장소를 입력해주세요"
                )
                Spacer(modifier = Modifier.height(16.dp))
                PaymentOtherComponent(
                    label = "카테고리",
                    placeholder = "카테고리를 선택해주세요",
                    clicked = { isCategoryShowModal = true },
                    value = category?.categoryName ?: ""
                )
                Spacer(modifier = Modifier.height(16.dp))
                key("transactionBalance") {
                    DefaultOutlinedTextField(
                        label = "금액",
                        onValueChange = { newValue ->
                            val filteredText = newValue.text.filter { it.isDigit() } // 숫자만 남기기
                            val textFieldValue = newValue.copy(text = filteredText)
                            viewModel.updateTransactionBalance(textFieldValue)
                        },
                        value = transactionBalance,
                        singleLine = true,
                        placeholder = "금액을 입력해주세요",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CurrencyVisualTransformation(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                PaymentOtherComponent(
                    label = "시간", placeholder = "시간을 선택해주세요", clicked = {
                        isTimeShowModal = true
                    }, value = formatTime, isTime = true
                )
            }
            Box(modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 24.dp)) {
                BottomGradientButton(content = buttonContent, enabled = isFormValid, onClick = {
                    focusManager.clearFocus()
                    Log.d("PaymentFormScreen", "submitForm: $paymentModel")
                    if (paymentModel == null) viewModel.createPayment()
                    else viewModel.updatePayment(paymentId = paymentModel.paymentId)

                })
            }
            if (isTimeShowModal) {
                OptionModalBottomSheet(content = {
                    Log.d("OptionModalBottomSheet", "PaymentFormScreen: $transactionTime")
                    val hasInitialTime = transactionTime.isNotEmpty()
                    val date = transactionTime.split(" ")[0]
                    val time = transactionTime.split(" ")[1]
                    var initialTime: LocalTime = LocalTime.now()
                    if (hasInitialTime) {
                        val splitTime = time.split(":")
                        val initialHour = splitTime[0].toInt()
                        val initialMin = splitTime[1].toInt()
                        initialTime = LocalTime.of(
                            initialHour, initialMin
                        )
                    }

                    TimeMinutePicker(initialTime = initialTime, onTimeSelected = {
                        focusManager.clearFocus()

                        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        viewModel.updateTransactionTime(buildString {
                            append(date)
                            append(" ")
                            append(
                                it.format(
                                    DateTimeFormatter.ofPattern(
                                        "HH:mm:ss"
                                    )
                                )
                            )
                        })
                        dismissModalBottomSheet(scope, sheetState) {
                            isTimeShowModal = false
                        }
                    })
                }, sheetState = sheetState, onDismissRequest = {
                    isTimeShowModal = false
                })
            }
            if (isCategoryShowModal) {
                OptionModalBottomSheet(content = {
                    CategoryComponent(category = category, onClicked = {
                        focusManager.clearFocus()
                        viewModel.updateCategoryName(it)
                        dismissModalBottomSheet(scope, sheetState) {
                            isCategoryShowModal = false
                        }
                    })

                }, sheetState = sheetState, onDismissRequest = {
                    isCategoryShowModal = false
                })
            }


        }
        when (uiState) {
            is PaymentFormUiState.Idle -> {

            }

            is PaymentFormUiState.Submitting -> {
                // Add a full-screen overlay with customized pointer input interceptor
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            // Intercept and consume all touch events
                            awaitPointerEventScope {
                                while (true) {
                                    // This will consume all pointer events
                                    awaitPointerEvent().changes.forEach { it.consume() }
                                }
                            }
                        }, color = Color.Black.copy(alpha = 0.1f) // Semi-transparent overlay
                ) {
                    // Optional loading indicator here
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WhiteTextColor)
                    }
                }
            }

            is PaymentFormUiState.SubmissionSuccess -> {
            }

            is PaymentFormUiState.SubmissionError -> {

            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryComponent(category: Category?, onClicked: (category: Category) -> Unit) {
    var selectedCategory: Category? by remember { mutableStateOf(category) }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(24.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3
        ) {
            Category.getAllCategories().map {
                CategoryItem(it, isSelected = selectedCategory == it, selectItem = {
                    selectedCategory = it
                })
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        DefaultTextButton(content = "선택하기", enabled = selectedCategory != null, onClick = {
            if (selectedCategory != null) onClicked(selectedCategory!!)
        })

    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, selectItem: () -> Unit) {
    val screenSize = ScreenSizeUtil.getScreenSizeDp()
    val width = screenSize.first.value / 4
    Box(modifier = Modifier
        .width(width.dp)
        .clickable { selectItem() }
        .background(color = if (isSelected) PrimaryColor else BottomModalSheetBackgroundColor)
        .border(1.dp, WhiteTextColor, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = category.imagePath,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = category.categoryName, style = Heading_M_SemiBold)

        }
    }

}


@Composable
fun PaymentOtherComponent(
    label: String,
    placeholder: String,
    value: String? = null,
    clicked: () -> Unit,
    isTime: Boolean = false,
) {

    Column {
        Text(
            label,
            modifier = Modifier.padding(bottom = 8.dp),
            style = Body_L_Medium.copy(color = WhiteTextColor)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { clicked() }
            .border(
                border = BorderStroke(1.dp, color = WhiteTextColor),
                shape = RoundedCornerShape(8.dp)
            ), contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    if (!value.isNullOrEmpty()) value else placeholder,
                    style = Body_L_Medium.copy(color = if (!value.isNullOrEmpty()) WhiteTextColor else Black200)
                )
                if (!isTime) Icon(imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = WhiteTextColor,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = 90f
                    })
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalSnapperApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeMinutePicker(
    initialTime: LocalTime = LocalTime.now(),
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 초기값(현재 시간)
    var selectedTime by remember { mutableStateOf(initialTime) }

    // ListState
    val hourListState = rememberLazyListState(selectedTime.hour)
    val minuteListState = rememberLazyListState(selectedTime.minute)

    // 변수
    val density = LocalDensity.current
    val threshold = remember { density.run { 20.dp.toPx() } } // 한 칸 높이의 절반

    val hour by remember {
        derivedStateOf {
            (hourListState.firstVisibleItemIndex + if (hourListState.firstVisibleItemScrollOffset >= threshold) 1 else 0) % 24
        }
    }

    val minute by remember {
        derivedStateOf {
            (minuteListState.firstVisibleItemIndex + if (minuteListState.firstVisibleItemScrollOffset >= threshold) 1 else 0) % 60
        }
    }

    // 시간이 변경되면 콜백 호출
    if (hour != selectedTime.hour || minute != selectedTime.minute) {
        selectedTime = LocalTime.of(hour, minute)
    }


    Column(modifier = Modifier.padding(24.dp)) {


        Box(
            contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()

        ) {

            Row(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                // 시간 선택 LazyColumn
                LazyColumn(
                    state = hourListState,
                    contentPadding = PaddingValues(16.dp, 80.dp),
                    flingBehavior = rememberSnapperFlingBehavior(hourListState),
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                ) {
                    items((0..23).toList()) { int ->
                        val textColor by animateColorAsState(
                            targetValue = if (int == hour) WhiteTextColor else Black200
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            BasicText(
                                text = String.format("%02d 시", int), style = Heading_L_Bold.copy(
                                    fontWeight = FontWeight.Normal, color = textColor
                                )
                            )
                        }
                    }
                }

                // 분 선택 LazyColumn
                LazyColumn(
                    state = minuteListState,
                    contentPadding = PaddingValues(16.dp, 80.dp),
                    flingBehavior = rememberSnapperFlingBehavior(minuteListState),
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                ) {
                    items((0..59).toList()) { int ->
                        val textColor by animateColorAsState(
                            targetValue = if (int == minute) WhiteTextColor else Black200
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            BasicText(
                                text = String.format("%02d 분", int), style = Heading_L_Bold.copy(
                                    fontWeight = FontWeight.Normal, color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        DefaultTextButton(content = "선택하기", onClick = {
            onTimeSelected(selectedTime)
        })
    }
}