package com.ssafy.sotory.presentation.auth.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 화면에 이벤트 처리
sealed interface AccountVerificationEvent {
    data object BankSelectOpen : AccountVerificationEvent  // 은행 선택
    data object BankSelectClose : AccountVerificationEvent // 닫기

    data object Request1WonTransferClicked : AccountVerificationEvent  // 1원 송금 요청
    data object RequestVerificationClicked : AccountVerificationEvent  // 인증 하기 버튼

    data class SelectBank(val selectedBank: String) : AccountVerificationEvent
    data class SelectedBankCheck(val selectedBank : String): AccountVerificationEvent
}

// 이벤트에서 화면 이동
sealed interface AccountVerificationNav {
//    data class ToDetail(val id: String) : NavigationAction
//    data class ToEdit(val id: String) : NavigationAction
}

data class Bank(
    val name: String,
    val logoResId: Int
)

@HiltViewModel
class AccountVerificationViewModel @Inject constructor() : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<AccountVerificationNav>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _accountNumber = MutableStateFlow(TextFieldValue())
    val accountNumber = _accountNumber.asStateFlow()

    private val _isAccountVerification = MutableStateFlow(false)
    val isAccountVerification = _isAccountVerification.asStateFlow()

    private val _isVerificationButtonEnabled = MutableStateFlow(false)
    val isVerificationButtonEnabled = _isVerificationButtonEnabled.asStateFlow()

    private val _isBankSelectBottomModalSheetOpen = MutableStateFlow(false)
    val isBankSelectBottomModalSheetOpen = _isBankSelectBottomModalSheetOpen.asStateFlow()

    private val _selectedBank = MutableStateFlow("")
    val selectedBank = _selectedBank.asStateFlow()

    private val _tempSelectedBank = MutableStateFlow("")
    val tempSelectedBank = _tempSelectedBank.asStateFlow()

    fun onEvent(event: AccountVerificationEvent) {
        when (event) {
            is AccountVerificationEvent.BankSelectOpen -> {
                _isBankSelectBottomModalSheetOpen.value = true
            }

            is AccountVerificationEvent.BankSelectClose -> {
                _isBankSelectBottomModalSheetOpen.value = false
                _tempSelectedBank.value = ""
            }

            is AccountVerificationEvent.SelectBank -> {
                _tempSelectedBank.value = event.selectedBank
//                _isBankSelectBottomModalSheetOpen.value = false
            }

            is AccountVerificationEvent.SelectedBankCheck -> {
                _selectedBank.value = _tempSelectedBank.value
                _tempSelectedBank.value = ""
                _isBankSelectBottomModalSheetOpen.value = false
            }


            is AccountVerificationEvent.Request1WonTransferClicked -> {
                _isAccountVerification.value = true
                _isVerificationButtonEnabled.value = false
                viewModelScope.launch {
                    delay(3000)
                    _isVerificationButtonEnabled.value = true
                }
            }

            is AccountVerificationEvent.RequestVerificationClicked -> {
            }

        }
    }

    fun updateAccountNumber(newAccountNumber: TextFieldValue) {
        _accountNumber.value = newAccountNumber
    }

    val BankList= listOf(
        Bank(name = "NH 농협", logoResId = R.drawable.logo_nh_nonghyup),
        Bank("카카오뱅크", R.drawable.logo_kakaobank),
        Bank("KB국민", R.drawable.logo_kb_kookmin),
        Bank("토스뱅크", R.drawable.logo_tossbank),
        Bank("신한", R.drawable.logo_sinhan),
        Bank("우리", R.drawable.logo_woori),
        Bank("IBK기업", R.drawable.logo_ibk_kiup),
        Bank("하나", R.drawable.logo_hana),
        Bank("새마을", R.drawable.logo_mg_semaeul),
        Bank("부산", R.drawable.logo_bnk_busan),
        Bank("iM뱅크(대구)", R.drawable.logo_im),
        Bank("케이뱅크", R.drawable.logo_kbank),
        Bank("신협", R.drawable.logo_sinhyup),
        Bank("우체국", R.drawable.logo_postoffice),
        Bank("SC제일", R.drawable.logo_sc_jeil),
        Bank("경남", R.drawable.logo_bnk_gyungnam),
        Bank("광주", R.drawable.logo_gwangju),
        Bank("수협", R.drawable.logo_suhyup)
    )
}