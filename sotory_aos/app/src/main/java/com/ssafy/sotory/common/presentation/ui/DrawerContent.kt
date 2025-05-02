package com.ssafy.sotory.common.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/**
 * 드로어에 들어갈 네비게이션 메뉴 컴포저블
 */
@Composable
fun DrawerContent(
    onNavigateToRecord: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToMyRoom: () -> Unit,
    onLogout: () -> Unit,
    drawerState: DrawerState? = null,
    currentRoute: String? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            // 앱 이름 또는 로고
            Text(
                text = "소토리",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, bottom = 24.dp)
            )

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // 기록 메뉴 아이템
            DrawerMenuItem(
                icon = Icons.Default.List,
                label = "기록",
                isSelected = currentRoute == "record",
                onClick = {
                    scope.launch {
                        drawerState?.close()
                    }
                    onNavigateToRecord()
                }
            )

            // 작성 메뉴 아이템
            DrawerMenuItem(
                icon = Icons.Default.Create,
                label = "작성",
                isSelected = currentRoute == "write",
                onClick = {
                    scope.launch {
                        drawerState?.close()
                    }
                    onNavigateToWrite()
                }
            )

            // 마이룸 메뉴 아이템
            DrawerMenuItem(
                icon = Icons.Default.AccountCircle,
                label = "마이룸",
                isSelected = currentRoute == "myroom",
                onClick = {
                    scope.launch {
                        drawerState?.close()
                    }
                    onNavigateToMyRoom()
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // 하단 추가 메뉴 (설정 등)
            DrawerMenuItem(
                icon = Icons.Default.Home,
                label = "로그아웃",
                isSelected = currentRoute == "home",
                onClick = {
                    scope.launch {
                        drawerState?.close()
                    }
                    onLogout()
                }
            )
        }
    }
}

/**
 * 드로어 메뉴의 각 아이템을 표시하는 컴포저블
 */
@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
