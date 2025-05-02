package com.ssafy.sotory.presentation.report.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.R
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TopMerchants(
    viewModel: ReportViewModel
) {
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)

    Box(
        modifier = Modifier
//            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "자주 찾는 장소", style = Heading_S_SemiBold)

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(report?.frequentMerchants ?: emptyList()) { merchant ->

                    val formattedAmount = String.format("%,d", merchant.amount)

                    val imageRes = when {
                        merchant.categoryName == "생활" -> R.drawable.icon_lifestyle
                        merchant.categoryName == "주유" -> R.drawable.gas
                        merchant.categoryName == "쇼핑" -> R.drawable.shopping
                        merchant.categoryName == "교육/육아" -> R.drawable.education
                        merchant.categoryName == "통신" -> R.drawable.communication
                        merchant.categoryName == "문화/여가" -> R.drawable.hobby
                        merchant.categoryName == "식비" -> R.drawable.meal
                        merchant.categoryName == "해외" -> R.drawable.overseas
                        merchant.categoryName == "교통" -> R.drawable.transportation
                        merchant.categoryName == "대형마트" -> R.drawable.mart
                        else -> R.drawable.icon_question
                    }

                    Box(
                        modifier = Modifier
                            .width(337.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = BottomBarBackgroundColor)
                            ,
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${merchant.rank}위", style = Heading_M_SemiBold)

                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = "생활 아이콘",
                                modifier = Modifier.size(52.dp)
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(text = merchant.merchantName, style = Heading_M_SemiBold)

                                    Text(text = merchant.categoryName, style = Body_L_Medium)
                                }

                                Text(
                                    text = "${merchant.visits}회 / ${formattedAmount}원",
                                    style = Body_L_Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
