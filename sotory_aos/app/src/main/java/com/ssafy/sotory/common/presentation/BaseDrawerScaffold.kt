package com.ssafy.sotory.common.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ssafy.sotory.common.presentation.ui.DrawerContent
import com.ssafy.sotory.ui.theme.BackgroundColor
import kotlinx.coroutines.launch


@Composable
fun BaseDrawerScaffold(
    onNavigateToRecord: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToMyRoom: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    drawerState: DrawerState = scaffoldState.drawerState,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {

    // 뒤로가기 버튼이 눌린 시간을 추적
    var backPressedTime by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Modify BackHandler to handle fade transition
    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        // 2초 이내에 두 번 뒤로가기 버튼이 눌렸는지 확인
        if (currentTime - backPressedTime < 2000) {
            // 앱 종료
            (context as? Activity)?.finish()
        } else {
            if (drawerState.isClosed) {
                // 첫 번째 뒤로가기 버튼이 눌린 시간 기록
                backPressedTime = currentTime
                // 사용자에게 알림
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "한 번 더 누르면 앱이 종료됩니다", duration = SnackbarDuration.Short
                    )
                }
            } else {
                scope.launch {
                    drawerState.close()
                }
            }
        }
    }


    Scaffold(scaffoldState = scaffoldState,
        backgroundColor = BackgroundColor,
        drawerContent = {
            DrawerContent(
                onNavigateToRecord = onNavigateToRecord,
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToMyRoom = onNavigateToMyRoom,
                onLogout = onLogout,
                drawerState = drawerState,
            )
        },
        drawerGesturesEnabled = true,
        snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        content(innerPadding, modifier)
    }

}