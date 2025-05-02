package com.ssafy.sotory.common.presentation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ssafy.sotory.ui.theme.Black400
import com.ssafy.sotory.ui.theme.BottomModalSheetBackgroundColor
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.WhiteTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoButtonDialog(
    content: String = "",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = false,
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 35.dp, bottom = 30.dp),
            colors = CardColors(
                containerColor = BottomModalSheetBackgroundColor,
                contentColor = WhiteTextColor,
                disabledContainerColor = Black400,
                disabledContentColor = WhiteTextColor,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = content, style = Heading_L_Bold, textAlign = TextAlign.Center,)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(52.dp),
                        onClick = onCancel,
                        colors = ButtonColors(
                            containerColor = WhiteTextColor,
                            contentColor = Black400,
                            disabledContentColor = Black400,
                            disabledContainerColor = WhiteTextColor
                        ),
                        enabled = true,
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Text(text = "취소", style = Heading_S_SemiBold)
                    }
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        onClick = onConfirm,
                        colors = ButtonColors(
                            containerColor = PrimaryColor,
                            contentColor = WhiteTextColor,
                            disabledContentColor = WhiteTextColor,
                            disabledContainerColor = PrimaryColor
                        ),
                        enabled = true,
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Text(text = "확인", style = Heading_S_SemiBold)
                    }
                }
            }

        }
    }
}