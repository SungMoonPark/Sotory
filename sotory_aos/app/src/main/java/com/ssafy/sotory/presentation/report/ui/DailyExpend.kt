package com.ssafy.sotory.presentation.report.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.domain.myroom.DailySpending
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil

@Composable
fun DailyExpend(
    viewModel: ReportViewModel
) {
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value

    Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ){
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(text = "일별 지출", style = Heading_S_SemiBold)
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
                ){
                if (report != null) {
                    DailyExpendLineChart(report.dailySpending)
                }
            }
//            Text(text = "지난 달보다 10만원 덜 썼어요.", style = Heading_S_SemiBold)
        }
    }
}

@Composable
fun DailyExpendLineChart(dailySpendingHistory: List<DailySpending>) {

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing) // 애니메이션 지속 시간
        )
    }

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        if (dailySpendingHistory.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 100f
        val graphWidth = width - padding
        val graphHeight = height - padding

        val maxAmount = dailySpendingHistory.maxOfOrNull { it.amount }?.toFloat() ?: return@Canvas
        val minAmount = 0f

        val yStepValue = if (maxAmount <= 50000) 10000f else 50000f
        val yMaxRounded = ceil(maxAmount / yStepValue) * yStepValue
        val yStepCount = (yMaxRounded / yStepValue).toInt()

        val pointDistance = if (dailySpendingHistory.size > 1) graphWidth / (dailySpendingHistory.size - 1) else 0f

        val normalizedPoints = dailySpendingHistory.mapIndexed { index, data ->
            val x = index * pointDistance + padding / 2
            val y = graphHeight - ((data.amount / yMaxRounded) * graphHeight) + padding / 2
            Offset(x, y)
        }

        if (normalizedPoints.isEmpty()) return@Canvas
        val currentIndex = (normalizedPoints.size * progress.value).toInt().coerceIn(0, normalizedPoints.size - 1)

        val gridColor = Color.Gray
        val xStep = pointDistance * 5  // X축 격자 간격 (5일 간격)

        // Y축 격자 (5만원 단위)
        for (i in 0..yStepCount) {
            val y = graphHeight - (i * (graphHeight / yStepCount)) + padding / 2
            drawLine(gridColor, Offset(padding / 2, y), Offset(graphWidth + padding / 2, y), strokeWidth = 2f)
        }

        // X축 격자 (5일 간격)
        for (i in 0 until dailySpendingHistory.size step 5) {
            val x = i * pointDistance + padding / 2
            drawLine(gridColor, Offset(x, padding / 2), Offset(x, graphHeight + padding / 2), strokeWidth = 2f)
        }

        // 내부 색칠
        val animatedFillPath = Path().apply {
            moveTo(normalizedPoints.first().x, graphHeight + padding / 2)
            normalizedPoints.subList(0, currentIndex + 1).forEach { lineTo(it.x, it.y) }
            lineTo(normalizedPoints[currentIndex].x, graphHeight + padding / 2)
            close()
        }

        drawPath(animatedFillPath, color = Color(0x802A96FA))

        // 꺾은선 그래프
        val animatedStrokePath = Path().apply {
            moveTo(normalizedPoints.first().x, normalizedPoints.first().y)
            for (i in 1..currentIndex) {
                lineTo(normalizedPoints[i].x, normalizedPoints[i].y)
            }
        }

        drawPath(animatedStrokePath, color = Color(0xFF2A96FA), style = Stroke(width = 3f))

        // X축 날짜
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)  // MAR 1, OCT 21 형식

        dailySpendingHistory.forEachIndexed { index, data ->
            val date = try {
                displayFormat.format(dateFormat.parse(data.date)!!)
            } catch (e: Exception) {
                data.date // 변환 실패 시 원본 날짜 "2025-03-01" 형식
            }

            val xPos = if (index == dailySpendingHistory.lastIndex) {
                normalizedPoints[index].x - 20f
            } else {
                normalizedPoints[index].x
            }

            if (index % 5 == 0 || index == dailySpendingHistory.lastIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    date.uppercase(),
                    xPos,
                    graphHeight + 80f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        // Y축 지출 내역 5만원 단위
        for (i in 0..yStepCount) {
            val value = (i * yStepValue).toInt()
            val y = graphHeight - (i * (graphHeight / yStepCount)) + padding / 2
            drawContext.canvas.nativeCanvas.drawText(
                value.toString(),
                40f,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }
    }
}