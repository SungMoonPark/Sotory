package com.ssafy.sotory.presentation.report.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.common.presentation.ui.DefaultModalBottomSheet
import com.ssafy.sotory.common.presentation.ui.DefaultTextButton
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.common.presentation.ui.textfield.DefaultUnderlinedTextNumberField
import com.ssafy.sotory.presentation.report.viewmodel.ReportEvent
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.ui.theme.Display_XL_Bold
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import com.ssafy.sotory.ui.theme.WhiteTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBudgetBottomModalSheet(
    viewModel: ReportViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    budget: Int?,
) {

    var budgetInput by remember {
        mutableStateOf(TextFieldValue(if (budget != null && budget > 0) (budget / 10000).toString() else ""))
    }

    DefaultModalBottomSheet(
        modifier = Modifier
//            .fillMaxHeight(0.5f)
//            .imePadding() // 또는 windowInsetsPadding(WindowInsets.ime) 도 가능
//            .fillMaxHeight()
        ,
        content = {
//            Box(
//                modifier = Modifier
////                    .fillMaxHeight()
//                    .fillMaxWidth()
////                    .height(300.dp)
//                    .padding(horizontal = 24.dp, vertical = 35.dp)
//            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "예산 설정", style = Display_XL_Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = WhiteTextColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DefaultUnderlinedTextNumberField(
                            onValueChange = {
                                if (it.text.length > 3) return@DefaultUnderlinedTextNumberField
                                budgetInput = it
                            },
                            textValue = budgetInput,
                            placeholder = "0",
                            modifier = Modifier.weight(1f),
                            textStyle = Heading_L_Bold
                        )

                        Text(text = "만원", style = Heading_L_Bold, color = WhiteTextColor)
                    }
                }
//            }
        },
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        buttons = {
            val newBudgetInTenThousand = budgetInput.text.toIntOrNull() ?: 0

            DefaultTextButton(
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .height(52.dp)
                    .fillMaxWidth()
                ,
                content = "확인",
                enabled = newBudgetInTenThousand > 0,
                onClick = {
                    val newBudget = newBudgetInTenThousand * 10000
                    viewModel.onEvent(event = ReportEvent.UpdateBudget(newBudget))
                    onDismiss()
                }
            )
        }
    )
}