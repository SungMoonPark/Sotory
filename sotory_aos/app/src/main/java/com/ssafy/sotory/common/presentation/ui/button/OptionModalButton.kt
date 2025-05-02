package com.ssafy.sotory.common.presentation.ui.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.Heading_M_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor

@Composable
fun OptionModalButton(onClick: () -> Unit, content: String) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(52.dp)
            .fillMaxWidth(),
    ) {
        Text(
            content, style = Heading_M_SemiBold.copy(color = WhiteTextColor)
        )
    }
}