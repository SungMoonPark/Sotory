package com.ssafy.sotory.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF8989FF)
val BottomModalSheetBackgroundColor = Color(0xFF515C8F)
val ActivateColorTo = Color(0xFFEFB8C8)
val ActivateColorFrom = Color(0xFFBE8FFF)
val DeactivateColor = Color(0xFF757ABF)
val BackgroundColor = Color(0xFF050F33)
val BottomBarBackgroundColor = Color(0xFF2A3959)
val Black100 = Color(0xFFD9D9D9)
val Black200 = Color(0xFF888888)
val Black300 = Color(0xFF555555)
val Black400 = Color(0xFF1A1A1A)
val WhiteTextColor = Color(0xFFFFFFFF)
val ErrorColor = Color(0xFFFF4C4C)

val SpecialColorStart = Color(0xFF8A8AFF) // 0%
val SpecialColorMiddle = Color(0xFFBC8EFF) // 50%
val SpecialColorEnd = Color(0xFF9178FE) // 100%

val SpecialColor = Brush.linearGradient(
    colors = listOf(SpecialColorStart, SpecialColorMiddle, SpecialColorEnd)
)

