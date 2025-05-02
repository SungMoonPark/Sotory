package com.ssafy.sotory.presentation.report.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ssafy.sotory.R
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold

@Composable
fun WordCloud(
    viewModel: ReportViewModel
){
    val report = viewModel.reportContent.collectAsStateWithLifecycle().value
    val keywordImageUrl = report?.keywordCloudUrl

    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ){
            Text(text = "일기 키워드", style = Heading_S_SemiBold)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                if (!keywordImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(keywordImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Word Cloud",
                        contentScale = ContentScale.FillWidth
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .size(363.dp)
//                            .fillMaxSize()
                    )
                } else {
                    Text(text = "키워드 이미지가 없습니다.")
                }
            }
        }
    }
}