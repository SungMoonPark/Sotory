package com.ssafy.sotory.presentation.settings.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.R
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun SettingsMainMenu(
    text: String,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = BottomBarBackgroundColor)
            .padding(horizontal = 16.dp, vertical = 11.dp)
            .noRippleClickable {
                Log.d("Debug", "설정 메뉴 버튼 클릭")
                onClick()
            }
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = text, style = Body_L_Medium)

            Image(
                painterResource(R.drawable.baseline_arrow_forward_ios_24),
                contentDescription = "바로 가기 아이콘",
                modifier = Modifier
                    .size(12.dp)
            )
        }
    }
}