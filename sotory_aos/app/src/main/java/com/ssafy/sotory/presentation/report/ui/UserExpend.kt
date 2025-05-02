package com.ssafy.sotory.presentation.report.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.R
import com.ssafy.sotory.presentation.report.viewmodel.ReportEvent
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Display_L_Bold
import com.ssafy.sotory.ui.theme.Display_XL_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun UserExpend(
    viewModel: ReportViewModel
) {
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value

    val nickname = viewModel.userNickname.collectAsStateWithLifecycle().value

    val monthlyExpend = when (report?.totalSpent) {
        null -> 0
        -1 -> "?"
        else -> report.totalSpent
    }
    val formattedExpend = if (monthlyExpend == "?") monthlyExpend else String.format("%,d", monthlyExpend)

    val yearMonth = viewModel.currentYearMonth.collectAsStateWithLifecycle().value
    val isNowMonth = viewModel.isNowMonth.collectAsStateWithLifecycle().value

    val isMinMonth = viewModel.isMinMonth.collectAsStateWithLifecycle().value
    val isMaxMonth = viewModel.isMaxMonth.collectAsStateWithLifecycle().value

    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            //
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = nickname, style = Display_XL_Bold)

                Text(text = "님 안녕하세요", style = Heading_M_SemiBold)
            }

            Column(
                modifier = Modifier
//                    .fillMaxWidth(),
                    ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                Row(
                    modifier = Modifier,
//                        .width(150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "이전 월로 이동",
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable {
                                if (!isMinMonth) {
                                    viewModel.onEvent(event = ReportEvent.ClickPreviousMonth)
                                }
                            },
                        alpha = if (isMinMonth) 0.3f else 1.0f

                    )

                    Text(
                        text = "${yearMonth.year}년 ${yearMonth.monthValue}월 지출",
                        style = Heading_M_SemiBold
                    )

                    Image(
                        painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = "다음 월로 이동",
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable {
                                if (!isMaxMonth) {
                                    viewModel.onEvent(event = ReportEvent.ClickNextMonth)
                                }
                            },
                        alpha = if (isMaxMonth) 0.3f else 1.0f
                    )
                }

                Text(text = "$formattedExpend 원", style = Display_L_Bold)
            }
        }
    }
}