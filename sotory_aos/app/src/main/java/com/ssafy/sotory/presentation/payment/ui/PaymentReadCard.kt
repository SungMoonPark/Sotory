package com.ssafy.sotory.presentation.payment.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.ui.theme.Black100
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Body_S_Regular
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable
import com.ssafy.sotory.util.formatDateTime
import com.ssafy.sotory.util.formatNumber

@Composable
fun PaymentReadCard(
    model: PaymentModel,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val rotationDegree by animateFloatAsState(
        targetValue = if (isExpanded) -90f else 90f, animationSpec = tween(
            durationMillis = 300, easing = FastOutSlowInEasing
        ), label = "rotation"
    )
    val number = model.transactionBalance.toIntOrNull() ?: 0 // 안전하게 변환
    val formattedBalance = formatNumber(number)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                BottomBarBackgroundColor, shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, BottomBarBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 13.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(model.merchantName, style = Heading_L_Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                formatDateTime(model.transactionTime), style = Body_S_Regular, color = Black100
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(model.categoryName, style = Body_S_Regular, color = Black100)
                Text(
                    formattedBalance + "원", style = Heading_M_SemiBold, color = WhiteTextColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = model.diary!!,
                        style = Body_L_Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (isExpanded) "닫기" else "자세히 보기", style = Body_S_Regular)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = WhiteTextColor,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotationDegree
                        })
                }
            }

        }
    }
}