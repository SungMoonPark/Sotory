package com.ssafy.sotory.presentation.diary

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomGradientButton
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultOutlinedTextField
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryEvent
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryFormUiState
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryHistoryFormViewModel
import com.ssafy.sotory.ui.theme.Black100
import com.ssafy.sotory.ui.theme.Body_S_Regular
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.util.formatDateTime
import com.ssafy.sotory.util.formatNumber

@Composable
fun DiaryEditScreen(
    model: PaymentModel, viewModel: DiaryHistoryFormViewModel, canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val diary by viewModel.diary.collectAsStateWithLifecycle()
    val valid by viewModel.isFormValid.collectAsStateWithLifecycle()
    // 한 번만 실행되는 효과
    LaunchedEffect(uiState) {
        when (uiState) {
            is DiaryFormUiState.SubmissionSuccess -> {
                Toast.makeText(context, "일기가 수정 됐습니다.", Toast.LENGTH_LONG).show()
                navigateUp()
            }

            is DiaryFormUiState.SubmissionError -> {

            }

            else -> {}
        }


//        if (uiState.apiState is ApiState.Success) {
//            // 성공 시 뒤로 가기 또는 다음 화면으로 이동
//            navigateUp()
//            Toast.makeText(context, "일기가 수정 됐습니다.", Toast.LENGTH_LONG).show()
//        } else if (uiState.apiState is ApiState.Error) {
//            val message = (uiState.apiState as ApiState.Error).message
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//            viewModel.resetSubmissionState()
//        }
    }
    Scaffold(topBar = {
        DefaultAppBar(
            title = "흔적 수정", navigateUp = navigateUp, canNavigateBack = canNavigateBack
        )
    }) { innerPadding ->
        LaunchedEffect(true) {
            model.diary?.let {
                viewModel.updateDiary(
                    TextFieldValue(
                        annotatedString = AnnotatedString(it), selection = TextRange(it.length)
                    )
                )
            }
        }
//        val enabled = uiState.isValid && uiState.apiState !is ApiState.Loading

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(model.merchantName, style = Heading_L_Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            formatDateTime(model.transactionTime),
                            style = Body_S_Regular,
                            color = Black100
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(model.categoryName, style = Body_S_Regular, color = Black100)
                            Text(
                                formatNumber(model.transactionBalance.toIntOrNull() ?: 0) + "원",
                                style = Heading_M_SemiBold,
                                color = WhiteTextColor
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        DefaultOutlinedTextField(
                            onValueChange = {
                                if (it.text.length > 500) return@DefaultOutlinedTextField
                                viewModel.updateDiary(it)
                            },
                            limits = 500,
                            value = diary, placeholder = "소비한 이야기를 알려주세요",
                        )
                    }
                }
                Box(modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 24.dp)) {
                    BottomGradientButton(content = "흔적 수정하기", enabled = valid, onClick = {
                        viewModel.onEvent(event = DiaryEvent.PatchDiary)
                    })
                }
            }
        }
    }
}