package com.ssafy.sotory.util

import java.text.NumberFormat
import java.util.Locale

fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.KOREA).format(number)
}