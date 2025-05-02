package com.ssafy.sotory.util
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat

object SystemBarUtil {
    /**
     * 상태 바(Status Bar) 높이를 dp 단위로 반환합니다.
     */
    @Composable
    fun getStatusBarHeight(): Dp {
        return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    }

    /**
     * 내비게이션 바(Navigation Bar) 높이를 dp 단위로 반환합니다.
     */
    @Composable
    fun getNavigationBarHeight(): Dp {
        return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    }

    /**
     * 상태 바 높이를 px 단위로 반환합니다.
     */
    @Composable
    fun getStatusBarHeightPx(): Float {
        val density = LocalDensity.current
        return with(density) { getStatusBarHeight().toPx() }
    }

    /**
     * 내비게이션 바 높이를 px 단위로 반환합니다.
     */
    @Composable
    fun getNavigationBarHeightPx(): Float {
        val density = LocalDensity.current
        return with(density) { getNavigationBarHeight().toPx() }
    }

    /**
     * 시스템 바 전체(상태 바 + 내비게이션 바) 높이를 반환합니다.
     */
    @Composable
    fun getSystemBarsHeight(): Pair<Dp, Dp> {
        val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        return Pair(topPadding, bottomPadding)
    }

    /**
     * 상태 바 높이를 dp 단위로 반환합니다. (대안 방법)
     */
    @Composable
    fun getStatusBarHeightAlternative(): Dp {
        val context = LocalContext.current
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        val heightPx = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }

        return with(LocalDensity.current) { heightPx.toDp() }
    }

    /**
     * Window로부터 직접 상태 바 높이를 가져옵니다.
     */
    @Composable
    fun getStatusBarHeightFromWindow(): Dp {
        val context = LocalContext.current
        val window = context.findWindow()
        val density = LocalDensity.current

        return if (window != null) {
            val insets = WindowInsetsCompat.toWindowInsetsCompat(window.decorView.rootWindowInsets)
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            with(density) { statusBarHeight.toDp() }
        } else {
            0.dp
        }
    }

    // Activity의 Window를 찾기 위한 확장 함수
    private fun Context.findWindow(): Window? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context.window
            }
            context = context.baseContext
        }
        return null
    }
}