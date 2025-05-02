package com.ssafy.sotory.common.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.sotory.PermissionViewModel
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.common.presentation.ui.PermissionCard
import com.ssafy.sotory.ui.theme.Body_M_Medium
import com.ssafy.sotory.ui.theme.Heading_L_Bold

@Composable
fun PermissionScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PermissionViewModel = hiltViewModel(),
) {

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val allGranted = grantedMap.values.all { it }
            onNavigateUp()
        }

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            Column(modifier.weight(1f)) {
                Spacer(Modifier.height(64.dp))
                Text("Sotory 권한 요청 안내", style = Heading_L_Bold)
                Spacer(Modifier.height(25.dp))
                Text("Sotory는 아래 권한들을 필요로 합니다.\n앱에서 권한 요청 시 허용해주세요.", style = Body_M_Medium)
                Spacer(Modifier.height(40.dp))
                PermissionCard(
                    icon = Icons.Default.Notifications,
                    permission = "알림 (선택)",
                    description = "일기카드 생성 알림에 필요합니다."
                )

            }

            Box(modifier = modifier.padding(bottom = 24.dp)) {
                DefaultTextButton(content = "확인", onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                        )
                    )
                    viewModel.saveIsFirstJoin(true)

                })
            }
        }
    }
}