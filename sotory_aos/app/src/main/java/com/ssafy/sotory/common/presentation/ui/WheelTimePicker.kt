package com.ssafy.sotory.common.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IOSStyleTimePickerComponent(
    initialTime: LocalTime = LocalTime.now(),
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(initialTime) }

    // 선택된 시간을 포맷팅하여 표시
    val formattedTime = remember(selectedTime) {
        selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = formattedTime,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("시간 선택") },
            readOnly = true,
            trailingIcon = {
                TextButton(onClick = { showDialog = true }) {
                    Text("변경")
                }
            }
        )

        if (showDialog) {
            IOSStyleTimePickerDialog(
                initialTime = selectedTime,
                onDismiss = { showDialog = false },
                onTimeSelected = { newTime ->
                    selectedTime = newTime
                    onTimeSelected(newTime)
                    showDialog = false
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IOSStyleTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    // 시간 및 분 상태
    var selectedHour by remember { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableIntStateOf(initialTime.minute) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "시간 선택",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // 선택된 시간 표시 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 시간 휠 피커
                        WheelTimePicker(
                            count = 24,
                            initValue = selectedHour,
                            onValueChange = { selectedHour = it },
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = ":",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // 분 휠 피커
                        WheelTimePicker(
                            count = 60,
                            initValue = selectedMinute,
                            onValueChange = { selectedMinute = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 중앙 선택 영역 표시
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newTime = LocalTime.of(selectedHour, selectedMinute)
                            onTimeSelected(newTime)
                        }
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun WheelTimePicker(
    count: Int,
    initValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 현재 선택된 값
    var selectedValue by remember { mutableIntStateOf(initValue) }

    // 변경될 때 콜백 호출
    if (selectedValue != initValue) {
        onValueChange(selectedValue)
    }

    VerticalPager(
        count = count,
        state = rememberInfiniteLoopingState(initValue, count),
        modifier = modifier.height(160.dp),
        contentPadding = PaddingValues(vertical = 60.dp)
    ) { page ->
        val currentValue = page % count

        // 중앙(선택된 값)에 가까울수록 알파값 증가
        val currentContext = this
        val alpha = remember(page, currentContext.currentPage) {
            val distance = minOf(
                abs(currentContext.currentPage - page),
                abs(currentContext.currentPage - page + count),
                abs(currentContext.currentPage - page - count)
            )
            if (distance <= 2) 1f - (distance * 0.2f) else 0.3f
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
                .alpha(alpha)
        ) {
            Text(
                text = String.format("%02d", currentValue),
                fontSize = 24.sp,
                fontWeight = if (alpha > 0.9f) FontWeight.Bold else FontWeight.Normal,
                color = if (alpha > 0.9f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            // 페이지가 정착되면 선택된 값 업데이트
            if (alpha > 0.9f && currentContext.currentPageOffset == 0f) {
                selectedValue = currentValue
            }
        }
    }
}

// 무한 루프 휠 피커를 위한 Pager 상태
@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberInfiniteLoopingState(initialPage: Int, pageCount: Int): com.google.accompanist.pager.PagerState {
    // 큰 숫자로 시작하여 "무한" 스크롤 느낌 제공
    val loopingCount = Int.MAX_VALUE / pageCount
    val startIndex = loopingCount / 2 * pageCount + initialPage

    return com.google.accompanist.pager.rememberPagerState(
        initialPage = startIndex
    )
}

// 절댓값 계산 함수
private fun abs(value: Int): Int {
    return if (value < 0) -value else value
}

// 사용 예시
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IOSStyleTimePickerExample() {
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IOSStyleTimePickerComponent(
            initialTime = selectedTime,
            onTimeSelected = { newTime ->
                selectedTime = newTime
                // 선택된 시간으로 작업 수행
                println("선택된 시간: $newTime")
            }
        )

        // 선택된 시간 표시
        Text(
            text = "선택된 시간: ${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}