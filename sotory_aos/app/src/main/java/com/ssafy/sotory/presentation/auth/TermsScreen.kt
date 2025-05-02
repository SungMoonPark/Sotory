package com.ssafy.sotory.presentation.auth

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.presentation.auth.ui.ProgressBar
import com.ssafy.sotory.presentation.auth.viewmodel.TermsEvent
import com.ssafy.sotory.presentation.auth.viewmodel.TermsNav
import com.ssafy.sotory.presentation.auth.viewmodel.TermsViewModel
import com.ssafy.sotory.presentation.payment.viewmodel.NavigationAction
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentEvent
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.Body_M_Regular
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable
import kotlinx.coroutines.launch

@Composable
fun TermsScreen(
    onNavigate: (TermsNav) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TermsViewModel = hiltViewModel()
) {

    val isCheckedAll by viewModel.isCheckedAll.collectAsState()
    val isCheckedTerms by viewModel.isCheckedTerms.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { action ->
            when (action) {
                is TermsNav.ToBack -> {
                    onNavigate(action)
                }

                is TermsNav.ToNext -> {
                    onNavigate(action)
                }

            }
        }
    }

    Scaffold(modifier = Modifier
        .background(BackgroundColor)
        .padding(), topBar = {
        DefaultAppBar(
            "약관 동의",
            actions = {

            },
        )
    }) { innerPadding ->


        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(
                modifier =
                modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.Top)
                ) {
                    // 회원 가입 진행도 단계
                    ProgressBar(currentStep = 1)
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
//                            .background(color = WhiteTextColor)
//                    ) { }
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .weight(1f)
//                            .background(color = WhiteTextColor)
//                    ) { }
//                }

                    // 약관
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 전체 동의
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    val y = size.height - strokeWidth / 2
                                    drawLine(
                                        color = WhiteTextColor, // 원하는 색상으로 변경 가능
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    Alignment.Start
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 체크 아이콘
                                Image(
                                    modifier = Modifier
                                        .noRippleClickable {
                                            coroutineScope.launch {
                                                viewModel.onEvent(TermsEvent.CheckAllTerms("all"))
                                            }
                                        }
                                        .size(32.dp),
                                    painter = painterResource(
                                        if (isCheckedAll) R.drawable.round_check_24_checked
                                        else R.drawable.round_check_24_unchecked
                                    ),
                                    contentDescription = "체크 아이콘"
                                )

                                Text("전체 동의", style = Heading_S_SemiBold)

                            }
                        }

                        // 약관 시작
                        Box {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterVertically
                                )
                            ) {
                                isCheckedTerms.forEachIndexed { index, isChecked ->
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    4.dp,
                                                    Alignment.Start
                                                ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    modifier = Modifier
                                                        .noRippleClickable {
                                                            coroutineScope.launch {
                                                                viewModel.onEvent(
                                                                    TermsEvent.CheckSelectedTerms(
                                                                        index
                                                                    )
                                                                )
                                                            }
                                                        }
                                                        .size(24.dp),
                                                    painter = painterResource(
                                                        if (isChecked) R.drawable.round_check_24_checked
                                                        else R.drawable.round_check_24_unchecked
                                                    ),
                                                    contentDescription = "체크 아이콘"
                                                )

                                                Text(
                                                    "[${if (index < 2) "필수" else "선택"}] 약관 ${index + 1}",
                                                    style = Body_M_Regular
                                                )
                                            }

                                            Text(
                                                text = "보기",
                                                style = Body_M_Regular.copy(textDecoration = TextDecoration.Underline),
                                                modifier = Modifier.noRippleClickable { }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BottomOneButton(
                text = "약관 동의",
                enabled = isCheckedTerms[0] && isCheckedTerms[1],
                onClick = {
                    coroutineScope.launch {
                        viewModel.onEvent(TermsEvent.ClickAgreement("agreement"))
                    }
                }
            )
        }
    }
}

