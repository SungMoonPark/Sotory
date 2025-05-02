package com.ssafy.sotory.presentation.payment.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import com.ssafy.sotory.domain.payment.PaymentModel

@Composable
fun PaymentCardList(
    modifier: Modifier = Modifier,
    paymentModels: List<PaymentModel>,
) {
//    Log.d("PaymentCardList", "paymentModels: ${paymentModels.size}")

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top)
    ) {
        items(paymentModels) { model ->
            model.diary?.let {
                PaymentReadCard(model = model)
            }
        }
    }
}