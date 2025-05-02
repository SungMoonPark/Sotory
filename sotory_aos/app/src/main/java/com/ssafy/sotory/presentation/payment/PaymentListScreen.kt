package com.ssafy.sotory.presentation.payment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.sotory.common.presentation.ui.DefaultAppBar
import com.ssafy.sotory.presentation.payment.ui.PaymentCardList
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentFetchViewModel
import com.ssafy.sotory.presentation.payment.viewmodel.PaymentReadUiState
import com.ssafy.sotory.util.formatDate
import com.ssafy.sotory.util.formatDateTime

@Composable
fun PaymentListScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentFetchViewModel = hiltViewModel(),
    date: String,
) {
    val paymentReadUiState by viewModel.paymentReadUiState.collectAsStateWithLifecycle()
    val title = formatDate(date)

    Scaffold(topBar = {
        DefaultAppBar(
            title = title,
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp
        )
    }) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            when (paymentReadUiState) {
                is PaymentReadUiState.Success -> {
                    val payments = (paymentReadUiState as PaymentReadUiState.Success).payments
                    Log.d("PaymentListScreen", "payments: ${payments.size}")
                    val existDiaryPayments = payments.filter {
                        it.diary != null
                    }

                    PaymentCardList(
                        modifier = modifier.fillMaxHeight(),
                        paymentModels = existDiaryPayments,
                    )
                }

                is PaymentReadUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is PaymentReadUiState.Error -> {
                    Text("error")
                }
            }

        }

    }


}