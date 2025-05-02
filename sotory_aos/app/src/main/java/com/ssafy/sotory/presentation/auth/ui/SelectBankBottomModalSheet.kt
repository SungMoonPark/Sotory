package com.ssafy.sotory.presentation.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.ui.DefaultModalBottomSheet
import com.ssafy.sotory.common.presentation.ui.button.BottomOneButton
import com.ssafy.sotory.presentation.auth.viewmodel.AccountVerificationViewModel
import com.ssafy.sotory.ui.theme.Body_S_Regular
import com.ssafy.sotory.ui.theme.Heading_L_Bold
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.presentation.auth.viewmodel.AccountVerificationEvent
import com.ssafy.sotory.ui.theme.ActivateColorFrom
import com.ssafy.sotory.ui.theme.Black400
import com.ssafy.sotory.ui.theme.DeactivateColor
import com.ssafy.sotory.ui.theme.PrimaryColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBankBottomModalSheet(
    viewModel: AccountVerificationViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState,
){

    val tempSelectedBank = viewModel.tempSelectedBank.collectAsStateWithLifecycle()
    val selectedBank = viewModel.selectedBank.collectAsStateWithLifecycle()

    DefaultModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.8f),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 35.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.Start
            ){
                Text(text = "은행을 선택해주세요", style = Heading_L_Bold)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), // 3열 고정
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(viewModel.BankList) { bank ->
                            Box(
                                modifier = Modifier
//                                    .padding(4.dp)
//                                    .background(color = DeactivateColor)
//                                    .clip(RoundedCornerShape(20.dp))
//                                    .border(
//                                        width = if ((selectedBank.value == "" && tempSelectedBank.value == bank.name) || (tempSelectedBank.value == "" && selectedBank.value == bank.name) || (selectedBank.value != "" && tempSelectedBank.value == bank.name)) 3.dp else 0.dp,
//                                        color = if ((selectedBank.value == "" && tempSelectedBank.value == bank.name) || (tempSelectedBank.value == "" && selectedBank.value == bank.name) || (selectedBank.value != "" && tempSelectedBank.value == bank.name)) ActivateColorFrom else Color.Transparent,
//                                        shape = RoundedCornerShape(20.dp)
//                                    )
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color = DeactivateColor)
                                    .border(
                                        width = if ((selectedBank.value == "" && tempSelectedBank.value == bank.name) || (tempSelectedBank.value == "" && selectedBank.value == bank.name) || (selectedBank.value != "" && tempSelectedBank.value == bank.name)) 3.dp else 0.dp,
                                        color = if ((selectedBank.value == "" && tempSelectedBank.value == bank.name) || (tempSelectedBank.value == "" && selectedBank.value == bank.name) || (selectedBank.value != "" && tempSelectedBank.value == bank.name)) ActivateColorFrom else Color.Transparent,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .noRippleClickable {
                                        viewModel.onEvent(event = AccountVerificationEvent.SelectBank(bank.name))
                                    }
                                    .size(90.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = bank.logoResId),
                                        contentDescription = "${bank.name} 로고",
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(text = bank.name, style = Body_S_Regular)
                                }
                            }
                        }
                    }
                }
            }

        },
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        buttons = {
            BottomOneButton(
                text = "확인",
                enabled = true, // 은행 선택하면 활성화
                onClick = {
                    viewModel.onEvent(
                        event = AccountVerificationEvent.SelectedBankCheck(
                            selectedBank = tempSelectedBank.value
                        )
                    )
                }
            )
        }
    )
}