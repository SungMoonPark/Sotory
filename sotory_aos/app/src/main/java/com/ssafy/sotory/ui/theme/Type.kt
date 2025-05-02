package com.ssafy.sotory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ssafy.sotory.R

val pretendardkr = FontFamily(
    Font(R.font.pretendard_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.pretendard_extra_bold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.pretendard_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendard_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendard_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.pretendard_extra_light, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.pretendard_thin, FontWeight.Thin, FontStyle.Normal),
)

val Display_XL_Bold = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
)

val Heading_L_Bold = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
)

val Heading_M_SemiBold = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
)

val Heading_S_SemiBold = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
)

val Heading_S_Medium = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
)

val Body_L_Medium = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)

val Display_L_Bold = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
)

val Body_M_Medium = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
)

val Body_S_Regular = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
)

val Body_M_Regular = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
)

val Caption_M_Medium = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
)

val Caption_S_Regular = TextStyle(
    fontFamily = pretendardkr, fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
)


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)