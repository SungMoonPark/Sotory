package com.ssafy.sotory.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultUnderlinedTextField
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultUnderlinedTextNumberField
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsProfileEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsProfileNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsProfileViewModel
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.Heading_S_SemiBold
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsProfileScreen(
    canNavigateBack: Boolean,
    onNavigate: (SettingsProfileNav) -> Unit,
    navigateUp: () -> Unit,
    viewModel: SettingsProfileViewModel = hiltViewModel()
) {
    val appbarTitle = "프로필 수정"

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(key1 = true) {
        viewModel.settingsProfileNav.collect { action ->
            when (action) {
                is SettingsProfileNav.ToBack -> {
                    onNavigate(action)
                }
            }
        }
    }

    Scaffold(topBar = {
        DefaultAppBar(
            title = appbarTitle, canNavigateBack = canNavigateBack, navigateUp = navigateUp
        )
    }) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    focusManager.clearFocus()
                }
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ){
                    Text(text = "닉네임을 변경하세요.", style = Heading_S_SemiBold)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = WhiteTextColor, // 원하는 색상 적용 가능
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val shape: Shape = RoundedCornerShape(8.dp)
                        val colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedTextColor = WhiteTextColor,
                            unfocusedTextColor = WhiteTextColor,
                            cursorColor = WhiteTextColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )

                        TextField(
                            onValueChange = {
                                if (it.text.length > 10) return@TextField
                                viewModel.updateUserName(it)
                            },
                            value = userName,
                            placeholder = { Text("변경할 이름을 입력하세요.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = colors,
                            shape = shape,
                            textStyle = Heading_L_Bold
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "전체 텍스트 삭제 아이콘",
                            tint = WhiteTextColor,
                            modifier = Modifier
                                .noRippleClickable {
                                    viewModel.onEvent(event = SettingsProfileEvent.ClickAllDelete)
                                }

                        )
                    }

                }
            }
            BottomOneButton(
                text = "수정하기",
                enabled = if (userName.text.isNotEmpty()) true else false,
                onClick = {
                    viewModel.onEvent(event = SettingsProfileEvent.ClickEditNickname)
                },
            )
        }

    }
}