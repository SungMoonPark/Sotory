package com.ssafy.sotory.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultUnderlinedTextNumberField
import com.ssafy.sotory.presentation.auth.ui.ProgressBar
import com.ssafy.sotory.presentation.auth.ui.SelectBankBottomModalSheet
import com.ssafy.sotory.presentation.auth.viewmodel.AccountVerificationEvent
import com.ssafy.sotory.presentation.auth.viewmodel.AccountVerificationViewModel
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.Black200
import com.ssafy.sotory.ui.theme.Black400
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Display_L_Bold
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountVerificationScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountVerificationViewModel,
    navController: NavHostController,
) {

    val focusManager = LocalFocusManager.current
    val accountNumber by viewModel.accountNumber.collectAsStateWithLifecycle()
    val isAccountVerification by viewModel.isAccountVerification.collectAsStateWithLifecycle()
    val isVerificationButtonEnabled by viewModel.isVerificationButtonEnabled.collectAsStateWithLifecycle()

    val isBankSelectBottomModalSheetOpen by viewModel.isBankSelectBottomModalSheetOpen.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedBank by viewModel.selectedBank.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier
        .background(BackgroundColor)
        .padding(), topBar = {
        DefaultAppBar(
            "본인 인증",
            actions = {
            },
        )
    }) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .weight(1f)
                    // 입력창 내리기
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.Top)
                ) {
                    // 회원 가입 진행도 단계 시작
                    ProgressBar(currentStep = 2)
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(4.dp),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .weight(1f)
//                            .background(color = PrimaryColor)
//                    ) { }
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .weight(1f)
//                            .background(color = PrimaryColor)
//                    ) { }
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .weight(1f)
//                            .background(color = WhiteTextColor)
//                    ) { }
//                }
                    // 회원 가입 진행도 단계 끝

                    // 은행 계좌 입력 부분 시작
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(36.dp)
                    ) {
                        // 상단 제목
                        Text(text = "본인 명의 은행 계좌를 \n입력해주세요.", style = Display_L_Bold)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            // 은행 선택 시작
                            Column(

                            ){
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .height(40.dp)
                                        .fillMaxWidth()
//                                        .drawBehind {
//                                            println("✅ drawBehind called for 은행 선택 영역")
//                                            val strokeWidth = 0.5.dp.toPx()
//                                            val y = size.height - strokeWidth / 2
//                                            drawLine(
////                                            color = WhiteTextColor, // 원하는 색상으로 변경 가능
//                                                color = Color.Red,
//                                                start = Offset(0f, y),
//                                                end = Offset(size.width, y),
//                                                strokeWidth = strokeWidth
//                                            )
//                                        }
                                        .noRippleClickable {
                                            viewModel.onEvent(event = AccountVerificationEvent.BankSelectOpen)
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (selectedBank == "") "은행 선택" else selectedBank,
                                        style = Heading_S_Medium.copy(color = Black200)
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                                        contentDescription = "Drop down arrow",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Divider(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    color = WhiteTextColor,
                                    thickness = 1.dp,
                                )
                            }
                            // 은행 선택 끝

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                DefaultUnderlinedTextNumberField(
                                    onValueChange = {
                                        if (it.text.length > 16) return@DefaultUnderlinedTextNumberField
                                        viewModel.updateAccountNumber(it)
                                    },
                                    textValue = accountNumber,
                                    placeholder = "- 없이 계좌번호 16자리를 입력해주세요",
                                    isReadOnly = isAccountVerification
                                )
                            }
                        }
                    }
                    // 은행 계좌 입력 부분 끝

                    // 인증 번호 입력 부분 시작
                    if (isAccountVerification) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(
                                16.dp,
                                Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(
                                        16.dp,
                                        Alignment.CenterVertically
                                    ),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = "인증 번호", style = Heading_S_SemiBold)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(4) {
                                            Box(
                                                modifier = Modifier
                                                    .size(62.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(color = Color(0xFFF0F5FA)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isVerificationButtonEnabled) {
                                                    Text(
                                                        text = "*",
                                                        style = Body_L_Medium.copy(
                                                            textDecoration = TextDecoration.Underline,
                                                            color = Black400
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 인증 번호 입력 부분 끝
                }
            }

            BottomOneButton(
                text = if (isAccountVerification) "인증하기" else "1원 송금 요청",
                enabled = if (!isAccountVerification) accountNumber.text.length == 16 else isVerificationButtonEnabled,
                onClick = {
                    if (!isAccountVerification) {
                        viewModel.onEvent(AccountVerificationEvent.Request1WonTransferClicked)
                    } else {
                        viewModel.onEvent(AccountVerificationEvent.RequestVerificationClicked)
                    }
                }
            )
        }

        if (isBankSelectBottomModalSheetOpen) {
            SelectBankBottomModalSheet(
                viewModel = viewModel,
                onDismiss = {
                    viewModel.onEvent(event = AccountVerificationEvent.BankSelectClose)
                },
                sheetState,
            )
        }

    }
}