package com.ssafy.sotory.presentation.report.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.domain.myroom.SpendingCategory
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.PrimaryColor

@Composable
fun CategoryExpend(
    viewModel: ReportViewModel
){
    val report = viewModel.reportContent.collectAsState().value

//    val colors = listOf(Color(0xFFFFA726), Color(0xFF66BB6A), PrimaryColor) // 각 섹션 색상

    val categories = report?.categoryBreakdown.orEmpty()
    val colors = List(categories.size) { index ->
        Color.hsv((index * 360f / categories.size) % 360, 0.5f, 0.9f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(text = "카테고리 별 지출", style = Heading_S_SemiBold)
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                report?.categoryBreakdown?.let { PieChart(it) }

                report?.categoryBreakdown?.forEachIndexed { index, category ->

                    val formattedCategoryAmount = String.format("%,d", category.amount)
                    val categoryPercent = category.percentage

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(modifier = Modifier.size(8.dp)) {
                                drawCircle(color = colors[index]) // 색상 리스트에서 순서대로 적용
                            }

                            Text(text = category.categoryName, style = Heading_S_SemiBold)
                        }

                        Text(text = "$formattedCategoryAmount 원 (${categoryPercent}%)", style = Body_L_Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<SpendingCategory>,
    modifier: Modifier = Modifier
) {
    val totalAmount = data.sumOf { it.amount }

    // 애니메이션 적용 (0f ~ 1f까지 증가)
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }

//    val colors = listOf(Color(0xFFFFA726), Color(0xFF66BB6A), PrimaryColor) // 각 섹션 색상

    val colors = List(data.size) { index ->
        Color.hsv((index * 360f / data.size) % 360, 0.5f, 0.9f)
    }

    Canvas(
        modifier = modifier.size(200.dp)
    ) {
        var startAngle = -90f // 시작 각도
        val radius = size.minDimension / 2f

        data.forEachIndexed { index, item ->
            val sweepAngle = (item.amount.toFloat() / totalAmount) * 360f * animatedProgress.value
            val midAngle = startAngle + sweepAngle / 2  // 섹션 중앙 각도

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height),
                style = Fill
            )

            val textX = (size.width / 2) + (radius * 0.6f) * kotlin.math.cos(Math.toRadians(midAngle.toDouble())).toFloat()
            val textY = (size.height / 2) + (radius * 0.6f) * kotlin.math.sin(Math.toRadians(midAngle.toDouble())).toFloat()

            // 🔥 작은 영역이면 바깥쪽으로 빼기
            val isSmallSection = sweepAngle < 20  // 특정 기준 이하일 경우 작은 영역으로 판단
            val finalTextX = if (isSmallSection) (size.width / 2) + (radius * 0.9f) * kotlin.math.cos(Math.toRadians(midAngle.toDouble())).toFloat() else textX
            val finalTextY = if (isSmallSection) (size.height / 2) + (radius * 0.9f) * kotlin.math.sin(Math.toRadians(midAngle.toDouble())).toFloat() else textY

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    item.categoryName,
                    finalTextX,
                    finalTextY,  // 🔥 살짝 위쪽으로 이동
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = if (isSmallSection) 20f else 40f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )

//                // 🔥 퍼센티지 출력 (카테고리명 아래)
//                drawText(
//                    "${item.percentage}%",
//                    finalTextX,
//                    finalTextY + 30,  // 🔥 살짝 아래쪽으로 이동
//                    android.graphics.Paint().apply {
//                        color = android.graphics.Color.DARKGRAY
//                        textSize = if (isSmallSection) 20f else 40f
//                        textAlign = android.graphics.Paint.Align.CENTER
//                    }
//                )
//                drawText(
//                    "${item.category}",
//                    finalTextX,
//                    finalTextY,
//                    android.graphics.Paint().apply {
//                        color = android.graphics.Color.BLACK
//                        textSize = if (isSmallSection) 20f else 40f // 작은 경우 글씨도 작게 조정
//                        textAlign = android.graphics.Paint.Align.CENTER
//                    }
//                )
            }

            // 🔥 작은 영역이면 선(Line)으로 연결
            if (isSmallSection) {
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(textX, textY),
                    end = androidx.compose.ui.geometry.Offset(finalTextX, finalTextY),
                    strokeWidth = 2f
                )
            }

            startAngle += sweepAngle // 다음 섹션을 위해 각도 업데이트
        }
    }
}