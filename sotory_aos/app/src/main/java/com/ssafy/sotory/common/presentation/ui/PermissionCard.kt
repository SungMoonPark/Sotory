package com.ssafy.sotory.common.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.ui.theme.Black100
import com.ssafy.sotory.ui.theme.Body_L_Medium
import com.ssafy.sotory.ui.theme.Body_M_Medium
import com.ssafy.sotory.ui.theme.Caption_M_Medium
import com.ssafy.sotory.ui.theme.WhiteTextColor

@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    permission: String,
    description: String,
) {
    Row {
        Icon(
            imageVector = icon,//
            contentDescription = "알림",
            modifier = modifier.size(32.dp),
            tint = WhiteTextColor
        )
        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(permission, style = Body_L_Medium.copy(color = WhiteTextColor))
            Spacer(modifier = Modifier.size(4.dp))
            Text(description, style = Caption_M_Medium.copy(color = Black100))
        }
    }
}