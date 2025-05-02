package com.ssafy.sotory.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ScreenSizeUtil {
    /**
     * 화면 크기를 dp 단위로 가져옵니다.
     * @return Pair<Dp, Dp> (너비, 높이)
     */
    @Composable
    fun getScreenSizeDp(): Pair<Dp, Dp> {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp.dp
        val screenHeightDp = configuration.screenHeightDp.dp

        return Pair(screenWidthDp, screenHeightDp)
    }

    /**
     * 화면 크기를 px 단위로 가져옵니다.
     * @return Pair<Float, Float> (너비, 높이)
     */
    @Composable
    fun getScreenSizePx(): Pair<Float, Float> {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current

        val screenWidthDp = configuration.screenWidthDp.dp
        val screenHeightDp = configuration.screenHeightDp.dp

        val screenWidthPx = with(density) { screenWidthDp.toPx() }
        val screenHeightPx = with(density) { screenHeightDp.toPx() }

        return Pair(screenWidthPx, screenHeightPx)
    }

    /**
     * dp 값을 px로 변환합니다.
     * @param dp 변환할 dp 값
     * @return Float 변환된 px 값
     */
    @Composable
    fun dpToPx(dp: Dp): Float {
        val density = LocalDensity.current
        return with(density) { dp.toPx() }
    }

    /**
     * px 값을 dp로 변환합니다.
     * @param px 변환할 px 값
     * @return Dp 변환된 dp 값
     */
    @Composable
    fun pxToDp(px: Float): Dp {
        val density = LocalDensity.current
        return with(density) { px.toDp() }
    }
}