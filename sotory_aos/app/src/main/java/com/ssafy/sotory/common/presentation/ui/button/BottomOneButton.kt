package com.ssafy.sotory.common.presentation.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.ui.theme.ActivateColorFrom
import com.ssafy.sotory.ui.theme.Black100
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.SpecialColor

@Composable
fun BottomOneButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color = ActivateColorFrom,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 35.dp),
        contentAlignment = Alignment.Center
    ) {
        DefaultTextButton(
            content = text, onClick = onClick, enabled = enabled, containerColor = containerColor
        )
    }
}


@Composable
fun BottomGradientButton(
    content: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush = SpecialColor,
) {
    Box() {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(52.dp)
                .fillMaxWidth()
                .padding(0.dp),
            enabled = enabled,
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = if (enabled) brush else Brush.linearGradient(
                            listOf(Black100, Black100)
                        ),
                    )
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = content, style = Heading_S_Medium)
            }
        }
    }
}