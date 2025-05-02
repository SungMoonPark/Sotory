package com.ssafy.sotory.presentation.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.R
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.BottomModalSheetBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun SettingsProfileCard(
    userName: String,
    birthDay: String?,
    gender: String?,
    onClick: () -> Unit
){
    val formattedBirthDay = birthDay?.let {
        if (it.length == 4) "${it.substring(0, 2)}.${it.substring(2)}" else it
    } ?: "생일 비공개"

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(text = "프로필", style = Heading_S_SemiBold)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color = BottomModalSheetBackgroundColor)
                .padding(horizontal = 12.dp, vertical = 31.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(19.5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("$userName ")
                            withStyle(
                                style = SpanStyle(
                                    color = WhiteTextColor,
                                    fontSize = Heading_M_SemiBold.fontSize,
                                    fontWeight = Heading_M_SemiBold.fontWeight
                                )
                            ) {
                                append("님")
                            }
                        },
                        style = Heading_L_Bold
                    )
                    Text(text = "$formattedBirthDay | $gender", style = Body_L_Medium)
                }

                Image(
                    painter = painterResource(R.drawable.outline_edit_square_24),
                    contentDescription = "프로필 수정 아이콘",
                    modifier = Modifier
                        .size(24.dp)
                        .noRippleClickable {
                            onClick()
                        }
                )
            }
        }
    }
}