package com.ssafy.sotory.presentation.report.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.R
import com.ssafy.sotory.presentation.report.viewmodel.ReportEvent
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Body_M_Medium
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MonthlyBudget(
//    viewModel: ReportViewModel
//){
//    val report = viewModel.reportContent.collectAsStateWithLifecycle().value
//
////    val monthlyExpend = if (report?.totalSpent != -1) report?.totalSpent  else "?"
////    val formattedExpend = if (monthlyExpend == "?") monthlyExpend else String.format("%,d", monthlyExpend)
////
//////    val budgetAmount = viewModel.dummyData.budgetAmount
////    val budgetAmount = if (report?.budgetAmount != -1) report?.budgetAmount else "예산 미설정"
//////    val formattedBudgetAmount = if (budgetAmount == "?") budgetAmount else budgetAmount?.let { String.format("%,d", it) } ?: "예산 미설정"
////    val formattedBudgetAmount = if (budgetAmount == "?") budgetAmount else budgetAmount?.let { String.format("%,d", it) }
////
////    val progress = if (budgetAmount != null) viewModel.dummyData.budgetPercentage?.times(0.01) else 0.0f
////    val restBudget = budgetAmount?.minus(monthlyExpend)
////    val formattedRestBudget = restBudget?.let { String.format("%,d", it) } ?: "계산 불가"
//////    val progress = if (budgetAmount != -1) report?.budgetPercentage else 0.0f
//////    val restBudget = if (budgetAmount != -1) budgetAmount?.minus(monthlyExpend) else
//////    val formattedRestBudget = restBudget?.let { String.format("%,d", it) } ?: "계산 불가"
////
////    val recommendedDailySpend by viewModel.recommendedDailySpend.collectAsStateWithLifecycle()
////    val formattedRecommendedDailySpend = String.format("%,d", recommendedDailySpend)
//
//    val yearMonth = viewModel.currentYearMonth.collectAsStateWithLifecycle().value
//
//    val monthlyExpend = report?.totalSpent?.takeIf { it != -1 } ?: null
//    val formattedExpend = monthlyExpend?.let { String.format("%,d", it) } ?: "?"
//
//    val budgetAmount = report?.budgetAmount?.takeIf { it != -1 } ?: null
//    val formattedBudgetAmount = budgetAmount?.let { String.format("%,d", it) } ?: "?"
//
//    val restBudgetTemp = if (budgetAmount != null && monthlyExpend != null) {
//        budgetAmount - monthlyExpend
//    } else null
//
////    val restBudget = if (restBudgetTemp != null && restBudgetTemp >= 0) {
////        restBudgetTemp
////    } else if (restBudgetTemp == null) {
////        0
////    } else {
////        "?"
////    }
//    val restBudgetInt = restBudget.toIntOrNull()
//
//    if (restBudgetInt != null && restBudgetInt > 0) {
//        // 예산 남았을 때
//        ...
//    } else if (restBudgetInt == 0) {
//        // 예산 0일 때
//        ...
//    } else {
//        // 불러온 소비내역 없을 때 또는 파싱 실패
//        ...
//    }
//
//    val formattedRestBudget = if (restBudget != "?") restBudget?.let { String.format("%,d", it.toInt()) } else  "?"
//
//    val progress = if (budgetAmount != -1 || budgetAmount != null) report?.budgetPercentage else 0.0f
//
//    val recommendedDailySpend by viewModel.recommendedDailySpend.collectAsStateWithLifecycle()
//    val formattedRecommendedDailySpend = if (budgetAmount != null) {
//        String.format("%,d", recommendedDailySpend)
//    } else {
//        "데이터가 없습니다"
//    }
//
//    val isNowMonth by viewModel.isNowMonth.collectAsStateWithLifecycle()
//
//    val isSettingBudgetBottomModalSheetOpen by viewModel.isSettingBudgetBottomModalSheetOpen.collectAsStateWithLifecycle()
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
//
//    Column(
//        modifier = Modifier.fillMaxWidth().padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ){
//            Text(text = "${yearMonth.monthValue}월 예산", style = Heading_M_SemiBold)
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Text(text = "$formattedBudgetAmount 원", style = Heading_S_Medium)
//
//                if (budgetAmount != null && monthlyExpend != null && isNowMonth) {
//                    Image(
//                        painter = painterResource(R.drawable.outline_edit_24),
//                        contentDescription = "예산 편집",
//                        modifier = Modifier.noRippleClickable {
//                            viewModel.onEvent(event = ReportEvent.OpenSettingBudgetBottomModalSheet)
//                        }
//                    )
//                }
//            }
//        }
//        val progressPercent = progress?.toFloat()?.times(0.01)
//
//        if (budgetAmount != 0) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(37.dp)
//                    .clip(RoundedCornerShape(4.dp))
//                    .background(WhiteTextColor) // 전체 바탕 흰색
//            ) {
//                if (progressPercent != null) {
//                    Box(
//                        modifier = Modifier
//                            .height(37.dp)
//                            .fillMaxWidth(progressPercent.toFloat()) // 진행률 만큼 너비 설정
//                            .clip(RoundedCornerShape(4.dp))
//                            .background(PrimaryColor), // 진행된 부분 색상 (연보라색)
//                        contentAlignment = Alignment.CenterEnd
//                    ) {
//                        Text(
//                            text = "${report?.budgetPercentage}%",
//                            style = Body_M_Medium,
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    }
//                }
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Canvas(modifier = Modifier.size(8.dp)) {
//                    drawCircle(color = PrimaryColor)
//                }
//
//                Text(text = "지출", style = Heading_S_Medium)
//            }
//
//            Text(text = "$formattedExpend 원", style = Body_L_Medium)
//        }
//
//        if (budgetAmount != 0) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Canvas(modifier = Modifier.size(8.dp)) {
//                        drawCircle(color = WhiteTextColor)
//                    }
//
//                    Text(text = "남은 예산", style = Heading_S_Medium)
//                }
//
//                Text(text = "$formattedRestBudget 원", style = Body_L_Medium)
//            }
//        }
//
//        if (budgetAmount != 0) {
//            if (restBudget != "?" && restBudget.toInt() > 0) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(color = BottomBarBackgroundColor),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Row(
//                        modifier = Modifier.padding(22.dp)
//                            .fillMaxWidth(),
////                horizontalArrangement = Arrangement.SpaceBetween,
//                        horizontalArrangement = Arrangement.spacedBy(
//                            32.dp,
//                            Alignment.CenterHorizontally
//                        ),
////                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.Start),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Image(
//                            painter = painterResource(R.drawable.icon_sunglasses_smiley),
//                            contentDescription = "Icon in Budget",
//                            modifier = Modifier.size(50.dp)
//                        )
//
//                        Text(
//                            text = buildAnnotatedString {
//                                append("하루 추천 지출은 ")
//                                withStyle(
//                                    style = SpanStyle(
//                                        color = PrimaryColor,
//                                        fontSize = Heading_M_SemiBold.fontSize,
//                                        fontWeight = Heading_M_SemiBold.fontWeight
//                                    )
//                                ) {
//                                    append(formattedRecommendedDailySpend)
//                                }
//                                append(" 원이에요")
//                            },
//                            style = Body_L_Medium
//                        )
//                    }
//                }
//            } else if (restBudget.toInt() == 0) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(color = BottomBarBackgroundColor),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(22.dp)
//                                .fillMaxWidth(),
////                horizontalArrangement = Arrangement.SpaceBetween,
//                            horizontalArrangement = Arrangement.spacedBy(
//                                40.dp,
//                                Alignment.CenterHorizontally
//                            ),
////                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.Start),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                painter = painterResource(R.drawable.icon_sad),
//                                contentDescription = "Icon in Budget",
//                                modifier = Modifier.size(50.dp)
//                            )
//
//                            Text(
//                                text = "예산을 다 썼어요",
//                                style = Heading_M_SemiBold
//                            )
//                        }
//                    }
//            } else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(color = BottomBarBackgroundColor),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Row(
//                        modifier = Modifier.padding(22.dp)
//                            .fillMaxWidth(),
////                horizontalArrangement = Arrangement.SpaceBetween,
//                        horizontalArrangement = Arrangement.spacedBy(
//                            40.dp,
//                            Alignment.CenterHorizontally
//                        ),
////                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.Start),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Image(
//                            painter = painterResource(R.drawable.icon_question),
//                            contentDescription = "Icon in Budget",
//                            modifier = Modifier.size(50.dp)
//                        )
//
//                        Text(
//                            text = "불러온 소비내역이 없어요",
//                            style = Heading_M_SemiBold
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    val budget by viewModel.budget.collectAsStateWithLifecycle()
//
//    if (isSettingBudgetBottomModalSheetOpen) {
//        SettingBudgetBottomModalSheet(
//            viewModel = viewModel,
//            onDismiss = {
//                viewModel.onEvent(event = ReportEvent.CloseSettingBudgetBottomModalSheet)
//            },
//            sheetState,
//            budget = budget
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBudget(viewModel: ReportViewModel) {
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value
    val yearMonth = viewModel.currentYearMonth.collectAsStateWithLifecycle().value
    val recommendedDailySpend by viewModel.recommendedDailySpend.collectAsStateWithLifecycle()
    val isNowMonth by viewModel.isNowMonth.collectAsStateWithLifecycle()
    val isSettingBudgetBottomModalSheetOpen by viewModel.isSettingBudgetBottomModalSheetOpen.collectAsStateWithLifecycle()
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val monthlyExpend = report?.totalSpent?.takeIf { it != -1 }
    val budgetAmount = report?.budgetAmount?.takeIf { it != -1 }
    val progress = report?.budgetPercentage?.takeIf { budgetAmount != null } ?: 0f
    val restBudget = budgetAmount?.let { it - (monthlyExpend ?: 0) }
    val restBudgetInt = restBudget?.coerceAtLeast(0)
    val progressPercent = progress.toFloat() * 0.01f

    val formattedBudgetAmount = budgetAmount?.let { "%,d".format(it) } ?: "?"
    val formattedExpend = monthlyExpend?.let { "%,d".format(it) } ?: "?"
    val formattedRestBudget = restBudgetInt?.let { "%,d".format(it) } ?: "?"
    val formattedRecommendedDailySpend = budgetAmount?.let { "%,d".format(recommendedDailySpend) } ?: "데이터가 없습니다"
    Log.d("restBudget", "restBudget 값은 $restBudget")
    Log.d("restBudgetInt", "restBudgetInt 값은 $restBudgetInt")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단: 예산 제목 + 편집 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${yearMonth.monthValue}월 예산", style = Heading_M_SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$formattedBudgetAmount 원", style = Heading_S_Medium)
                if (budgetAmount != null && monthlyExpend != null && isNowMonth) {
                    Image(
                        painter = painterResource(R.drawable.outline_edit_24),
                        contentDescription = "예산 편집",
                        modifier = Modifier.noRippleClickable {
                            viewModel.onEvent(ReportEvent.OpenSettingBudgetBottomModalSheet)
                        }
                    )
                }
            }
        }

        // 진행률 바
        if (budgetAmount != 0 && progressPercent > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(37.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(WhiteTextColor)
            ) {
                Box(
                    modifier = Modifier
                        .height(37.dp)
                        .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryColor),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text("${report?.budgetPercentage}%", style = Body_M_Medium, modifier = Modifier.padding(8.dp))
                }
            }
        }

        // 지출 금액
        LabeledAmount(label = "지출", color = PrimaryColor, amount = formattedExpend)

        // 남은 예산
        if (budgetAmount != 0) {
            LabeledAmount(label = "남은 예산", color = WhiteTextColor, amount = formattedRestBudget)
        }

        // 하단 메시지 영역
        if (budgetAmount != 0) {
            BudgetStatusMessage(restBudgetInt, formattedRecommendedDailySpend, isNowMonth)
        }
    }

    // 바텀 시트
    if (isSettingBudgetBottomModalSheetOpen) {
        SettingBudgetBottomModalSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.onEvent(ReportEvent.CloseSettingBudgetBottomModalSheet) },
            sheetState = sheetState,
            budget = report?.budgetAmount
        )
    }
}

@Composable
private fun LabeledAmount(label: String, color: androidx.compose.ui.graphics.Color, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = color) }
            Text(label, style = Heading_S_Medium)
        }
        Text("$amount 원", style = Body_L_Medium)
    }
}

@Composable
private fun BudgetStatusMessage(
    restBudget: Int?,
    formattedDaily: String,
    isNowMonth: Boolean
) {
    Log.d("restBudget", "예산 상태 메세지 $restBudget")
    val (iconRes, message) = when {
        restBudget == null -> R.drawable.icon_question to "연동된 소비내역이 없어요"
        restBudget > 0 && isNowMonth -> R.drawable.icon_sunglasses_smiley to null
        restBudget > 0 && !isNowMonth -> R.drawable.icon_sunglasses_smiley to "예산을 잘 지켰군요!"
        restBudget == 0 -> R.drawable.icon_sad to "예산을 다 썼어요"
        restBudget < 0 -> R.drawable.icon_sad to "예산을 다 썼어요"
        else -> R.drawable.icon_question to "연동된 소비내역이 없어요"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            if (message != null) {
                Text(message, style = Heading_M_SemiBold)
            } else {
                Text(
                    text = buildAnnotatedString {
                        append("하루 추천 지출은 ")
                        withStyle(
                            SpanStyle(
                                color = PrimaryColor,
                                fontSize = Heading_M_SemiBold.fontSize,
                                fontWeight = Heading_M_SemiBold.fontWeight
                            )
                        ) {
                            append(formattedDaily)
                        }
                        append(" 원이에요")
                    },
                    style = Body_L_Medium
                )
            }
        }
    }
}
