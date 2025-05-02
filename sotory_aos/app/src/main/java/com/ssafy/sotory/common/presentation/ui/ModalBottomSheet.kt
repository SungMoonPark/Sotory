package com.ssafy.sotory.common.presentation.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.sotory.ui.theme.BottomModalSheetBackgroundColor
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultModalBottomSheet(
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    buttons: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
) {
//    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        dragHandle = null,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = BottomModalSheetBackgroundColor,
        properties = ModalBottomSheetDefaults.properties(shouldDismissOnBackPress = false),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
    ) {

//        Column(
//            modifier = Modifier.background(BottomModalSheetBackgroundColor),
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
////            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)
//        ) {
//            Box(
//                modifier = Modifier.weight(1f).padding(bottom = 0.dp)
//            ){
//                content()
//            }
//            Row(modifier = Modifier,
////                .weight(1f),
//                content = buttons)
//        }
        Column(
            modifier = Modifier
                .background(BottomModalSheetBackgroundColor)
                .padding(horizontal = 24.dp, vertical = 40.dp),
//            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // content 영역을 고정 height 또는 heightIn으로 조절
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
//                    .heightIn(min = 200.dp, max = 300.dp) // 적절한 높이 제한
            ) {
                content()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                content = buttons
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionModalBottomSheet(
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
) {
//    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        dragHandle = null,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = BottomModalSheetBackgroundColor,
        properties = ModalBottomSheetDefaults.properties(shouldDismissOnBackPress = false),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .background(BottomModalSheetBackgroundColor)
                .padding(vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            content()

        }
    }
}