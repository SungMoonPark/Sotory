package com.ssafy.sotory.common.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor


@SuppressLint("RestrictedApi", "StateFlowValueCalledInComposition")
@Composable
fun DefaultAppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundColor,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        elevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = { Text(text = title, style = Heading_S_SemiBold, color = WhiteTextColor) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = WhiteTextColor
                    )
                }
            } else {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null,
                        tint = WhiteTextColor
                    )
                }
            }
        },
        actions = actions
    )
}

fun isOnlyCurrentScreenInBackStack(navController: NavController): Boolean {
    return navController.previousBackStackEntry == null
}