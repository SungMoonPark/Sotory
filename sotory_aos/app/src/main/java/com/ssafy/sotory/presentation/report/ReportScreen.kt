package com.ssafy.sotory.presentation.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.presentation.report.ui.CategoryExpend
import com.ssafy.sotory.presentation.report.ui.DailyExpend
import com.ssafy.sotory.presentation.report.ui.MonthlyBudget
import com.ssafy.sotory.presentation.report.ui.TopMerchants
import com.ssafy.sotory.presentation.report.ui.UserExpend
import com.ssafy.sotory.presentation.report.ui.WordCloud
import com.ssafy.sotory.presentation.report.viewmodel.ReportEvent
import com.ssafy.sotory.presentation.report.viewmodel.ReportNav
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Display_XL_Bold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable
import kotlinx.coroutines.launch

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    onNavigate: (ReportNav) -> Unit,
    drawerState: DrawerState,
) {
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        val yearMonth = viewModel.currentYearMonth.value
        viewModel.fetchReport(yearMonth.year, yearMonth.monthValue)
    }

    LaunchedEffect(key1 = true) {
        viewModel.reportNav.collect { action ->
            when (action) {
                is ReportNav.ToBack -> {
                    onNavigate(action)
                }

                is ReportNav.ToSetting -> {
                    onNavigate(action)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .background(BackgroundColor)
            .padding(),
        topBar = {
            DefaultAppBar(
                "마이룸",
                actions = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "설정 아이콘",
                            tint = WhiteTextColor,
                            modifier = Modifier
                                .size(22.dp)
                                .noRippleClickable {
                                    viewModel.onEvent(event = ReportEvent.ClickSettingsButton)
                                }
                        )
                    }
                },
                navigateUp = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            item { UserExpend(viewModel) }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(color = BottomBarBackgroundColor)
                )
            }

            item { MonthlyBudget(viewModel) }

            if (!report?.categoryBreakdown.isNullOrEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(color = BottomBarBackgroundColor)
                    )
                }


                item { CategoryExpend(viewModel) }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(color = BottomBarBackgroundColor)
                    )
                }
            }

            if (!report?.frequentMerchants.isNullOrEmpty()) {
                item { TopMerchants(viewModel) }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(color = BottomBarBackgroundColor)
                    )
                }
            }

            if (!report?.dailySpending.isNullOrEmpty()) {
                item { DailyExpend(viewModel) }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(color = BottomBarBackgroundColor)
                    )
                }
            }

            if (!report?.keywordCloudUrl.isNullOrBlank()) {
                item { WordCloud(viewModel) }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Transparent)
                    )
                }
            }

//            if (report?.categoryBreakdown.isNullOrEmpty()) {
//                item {
//                    Box(
//                        modifier = Modifier
//                            .padding(24.dp)
//                            .fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Image(
//                            painterResource(R.drawable.sotory_logo),
//                            contentDescription = "sad emoticon",
//                            modifier = Modifier.size(200.dp)
//                        )
//                    }
//                }
//
//                item {
//                    Box(
//                        modifier = Modifier
//                            .padding(24.dp)
//                            .fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(text = "연동된 소비 데이터가 없어요", style = Display_XL_Bold)
//                    }
//                }
//
//            }
        }
    }
}