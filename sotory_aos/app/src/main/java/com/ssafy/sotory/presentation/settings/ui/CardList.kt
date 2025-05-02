package com.ssafy.sotory.presentation.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.domain.creditcard.ConnectedCardListModel
import com.ssafy.sotory.domain.creditcard.OwnedCardListModel
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListEvent
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListViewModel
import com.ssafy.sotory.ui.theme.Heading_S_Medium
import com.ssafy.sotory.ui.theme.WhiteTextColor
import com.ssafy.sotory.ui.theme.noRippleClickable

@Composable
fun MyCardList(
    viewModel: SettingsCardListViewModel
){
    // 연결된 나의 카드

    val connectedCardList by viewModel.connectedCardList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items (connectedCardList) { card ->
            MyCardInfo(viewModel, card)
        }
    }
}

@Composable
fun AddingCardList(
    viewModel: SettingsCardListViewModel
){
    // 내가 보유하고 있는 카드

    val ownedCardList by viewModel.ownedCardList.collectAsState()


    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items (ownedCardList) { card ->
            AddingCardInfo(viewModel, card)
        }
    }
}

@Composable
fun MyCardInfo(
    viewModel: SettingsCardListViewModel,
    cardData: ConnectedCardListModel
){

    // 연결된 나의 카드

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            Modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painterResource(viewModel.getCardLogoResource(cardData.cardIssuerCode)),
                contentDescription = "bank logo",
                modifier = Modifier.size(30.dp)
            )

            Text(text = cardData.cardName, style = Heading_S_Medium)

        }

        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = "삭제 아이콘",
            tint = WhiteTextColor,
            modifier = Modifier
                .size(20.dp)
                .noRippleClickable {
                    viewModel.onEvent(event = SettingsCardListEvent.ClickDeleteCard(cardData.cardId, cardData.cardName))
                }
        )

    }
}

@Composable
fun AddingCardInfo(
    viewModel: SettingsCardListViewModel,
    cardData: OwnedCardListModel
){
    // 보유하고 있는 나의 카드
    val selectedCardNos by viewModel.selectedCardNos.collectAsState()
    val isSelected = selectedCardNos.contains(cardData.cardNo)
    val isConnected = viewModel.isCardConnected(cardData.cardNo)

    val alpha = if (isConnected) 0.5f else 1.0f

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            Modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painterResource(viewModel.getCardLogoResource(cardData.cardIssuerCode)),
                contentDescription = "bank logo",
                modifier = Modifier.size(30.dp)
            )

            Text(text = cardData.cardName, style = Heading_S_Medium)

        }

        Icon(
            imageVector = if (isSelected || isConnected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = "체크박스 아이콘",
            tint = WhiteTextColor,
            modifier = Modifier
                .size(20.dp)
                .alpha(alpha)
                .let {
                    if (!isConnected) {
                        it.noRippleClickable {
                            viewModel.onEvent(SettingsCardListEvent.ClickCheckBoxAddingCard(cardData.cardNo))
                        }
                    } else {
                        it // 이미 연결된 경우 클릭 불가
                    }
                }
        )
    }
}