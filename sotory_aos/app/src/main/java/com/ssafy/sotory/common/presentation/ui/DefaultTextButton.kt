package com.ssafy.sotory.common.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.ActivateColorFrom
import com.ssafy.sotory.ui.theme.Black100
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.WhiteTextColor

@Composable
fun DefaultTextButton(
    content: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .height(52.dp)
        .fillMaxWidth()
        .semantics { contentDescription = "$content button" },
    containerColor: Color = ActivateColorFrom,
) {
    val colors = ButtonColors(
        containerColor = containerColor,
        contentColor = WhiteTextColor,
        disabledContentColor = WhiteTextColor,
        disabledContainerColor = Black100
    )
    TextButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = content, style = Heading_S_Medium)
    }
}