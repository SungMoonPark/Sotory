package com.ssafy.sotory.presentation.settings.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.R
import com.ssafy.sotory.common.presentation.LoggingViewModel
import com.ssafy.sotory.data.ResponseResult
import com.ssafy.sotory.data.dto.creditcard.CardRegistrationRequest
import com.ssafy.sotory.domain.creditcard.OwnedCardListModel
import com.ssafy.sotory.domain.creditcard.ConnectedCardListModel
import com.ssafy.sotory.domain.creditcard.CreditCardRepository
import com.ssafy.sotory.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 화면에 이벤트 처리
sealed interface SettingsCardListEvent {
    data class ClickDeleteCard(val id: String, val name: String): SettingsCardListEvent  // 뒤로 가기 버튼 클릭
    data object ClickDeleteCardDialogDismiss: SettingsCardListEvent // 카드 삭제 다이얼로그 취소
    data class ClickDeleteCardDialogCheck(val id: String): SettingsCardListEvent // 카드 삭제 다이얼로그 확인
    data class ClickCheckBoxAddingCard(val id: String): SettingsCardListEvent  // 체크 박스 클릭
    data object ClickAddCards: SettingsCardListEvent  // 최종 카드 연동에 추가하기 버튼
    data object AddCardToConnect : SettingsCardListEvent  // 상단 바 "추가" 클릭 - 페이지 변환
}

// 이벤트에서 화면 이동
sealed interface SettingsCardListNav {
    data object ToAddCardToConnect : SettingsCardListNav  // 연결 카드 추가하러 가기
    data object ReturnToMyCard : SettingsCardListNav  // 카드 추가 후 연결 카드로 되돌아오기
}

@HiltViewModel
data class SettingsCardListViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val creditCardRepository: CreditCardRepository
): LoggingViewModel() {

    private val _ownedCardList = MutableStateFlow<List<OwnedCardListModel>>(emptyList())
    val ownedCardList = _ownedCardList.asStateFlow()

    private val _connectedCardList = MutableStateFlow<List<ConnectedCardListModel>>(emptyList())
    val connectedCardList = _connectedCardList.asStateFlow()

    private val _settingsCardListNav = MutableSharedFlow<SettingsCardListNav>()
    val settingsCardListNav = _settingsCardListNav.asSharedFlow()

    private val _isDeleteCardDialogOpen = MutableStateFlow(false)
    val isDeleteCardDialogOpen: StateFlow<Boolean> = _isDeleteCardDialogOpen.asStateFlow()

    private val _targetDeleteCardId = MutableStateFlow<String?>(null)
    val targetDeleteCardId = _targetDeleteCardId.asStateFlow()

    private val _targetDeleteCardName = MutableStateFlow<String?>(null)
    val targetDeleteCardName = _targetDeleteCardName.asStateFlow()

    private val _selectedCardNos = MutableStateFlow<Set<String>>(emptySet())
    val selectedCardNos: StateFlow<Set<String>> = _selectedCardNos.asStateFlow()

    fun onEvent(event: SettingsCardListEvent) {
        when (event) {
            is SettingsCardListEvent.AddCardToConnect -> {
                viewModelScope.launch {
                    _settingsCardListNav.emit(SettingsCardListNav.ToAddCardToConnect)
                }
            }

            is SettingsCardListEvent.ClickDeleteCard -> {
                _targetDeleteCardId.value = event.id
                _targetDeleteCardName.value = event.name
                _isDeleteCardDialogOpen.value = true
            }

            is SettingsCardListEvent.ClickDeleteCardDialogDismiss -> {
                _isDeleteCardDialogOpen.value = false
                _targetDeleteCardId.value = null
            }

            is SettingsCardListEvent.ClickDeleteCardDialogCheck -> {
                viewModelScope.launch {
                    deleteConnectedCard(event.id)
                }
                _isDeleteCardDialogOpen.value = false
                _targetDeleteCardId.value = null
            }

            is SettingsCardListEvent.ClickCheckBoxAddingCard -> {
                // 어딘가에 체크박스 클릭한 카드 데이터들(리스트 형태로 추가하기에 넣을 항목들)에 추가
                val current = _selectedCardNos.value.toMutableSet()
                val cardNo = event.id  // id → cardNo로 바꿔 전달된다고 가정
                if (current.contains(cardNo)) {
                    current.remove(cardNo)
                } else {
                    current.add(cardNo)
                }
                _selectedCardNos.value = current
            }

            is SettingsCardListEvent.ClickAddCards -> {
                viewModelScope.launch {
                    // 카드 추가하는 api
                    postCard()
                    _settingsCardListNav.emit(SettingsCardListNav.ReturnToMyCard)
                }
            }
        }
    }

    fun fetchOwnedCards(){
        viewModelScope.launch {
            when (val result = creditCardRepository.getMyOwnedCard()){
                is ResponseResult.Success -> {
                    _ownedCardList.value = result.data.data
                    Log.d("fetchOwnedCards", "fetchOwnedCards 불러오기 성공: ${result.data}")
                }

                is ResponseResult.ServerError -> {
                    Log.e("fetchOwnedCards", "fetchOwnedCards 서버 오류 - code: ${result.code}, msg: ${result.message}")
                }

                is ResponseResult.Exception -> {
                    Log.e("fetchOwnedCards", "fetchOwnedCards 예외 발생 - ${result.message}", result.e)
                }
            }
        }
    }

    fun fetchConnectedCards(){
        viewModelScope.launch {
            when (val result = creditCardRepository.getMyConnectedCard()){
                is ResponseResult.Success -> {
                    _connectedCardList.value = result.data
                    Log.d("fetchConnectedCards", "fetchConnectedCards 불러오기 성공: ${result.data}")
                }

                is ResponseResult.ServerError -> {
                    Log.e("fetchConnectedCards", "fetchConnectedCards 서버 오류 - code: ${result.code}, msg: ${result.message}")
                }

                is ResponseResult.Exception -> {
                    Log.e("fetchConnectedCards", "fetchConnectedCards 예외 발생 - ${result.message}", result.e)
                }
            }
        }
    }

    fun postCard(){
        viewModelScope.launch {
            val ownedCards = ownedCardList.value
            val connectedCards = connectedCardList.value
            val selectedCardNos = selectedCardNos.value

            val connectedCardNos = connectedCards.map { it.cardNo }.toSet()

            val newCards = ownedCards.filter {
                it.cardNo !in connectedCardNos && it.cardNo in selectedCardNos
            }

            if (newCards.isEmpty()) {
                Log.d("postCard", "추가할 카드가 없습니다 (이미 연결된 카드 제외됨)")
                return@launch
            }

            val request = newCards.map {
                CardRegistrationRequest(
                    cardNo = it.cardNo,
                    cardIssuerCode = it.cardIssuerCode,
                    cardIssuerName = it.cardIssuerName,
                    cardName = it.cardName
                )
            }

            when (val result = creditCardRepository.postCard(request)) {
                is ResponseResult.Success -> {
                    Log.d("postCard", "카드 등록 성공")
                    _selectedCardNos.value = emptySet()
                    _settingsCardListNav.emit(SettingsCardListNav.ReturnToMyCard)
                }

                is ResponseResult.ServerError -> {
                    Log.e("postCard", "서버 오류 - code: ${result.code}, msg: ${result.message}")
                }

                is ResponseResult.Exception -> {
                    Log.e("postCard", "예외 발생 - ${result.message}", result.e)
                }
            }
        }
    }

    fun deleteConnectedCard(id: String){
        viewModelScope.launch {
            when (val result = creditCardRepository.deleteCard(id)) {
                is ResponseResult.Success -> {
                    // 삭제 성공 시 목록 새로고침
                    fetchConnectedCards()
                    Log.d("deleteConnectedCard", "카드 삭제 성공")
                }

                is ResponseResult.ServerError -> {
                    Log.e("deleteConnectedCard", "서버 오류 - code: ${result.code}, msg: ${result.message}")
                }

                is ResponseResult.Exception -> {
                    Log.e("deleteConnectedCard", "예외 발생 - ${result.message}", result.e)
                }
            }
        }
    }

    fun isCardConnected(cardNo: String): Boolean {
        val connectedCardNos = connectedCardList.value.map { it.cardNo }
        val result = connectedCardNos.contains(cardNo)

        Log.d("isCardConnected", """
        🔍 검사 중인 카드 번호: $cardNo
        연결된 카드 번호 목록: $connectedCardNos
        결과: $result
    """.trimIndent())

        return result
    }

    fun getCardLogoResource(cardIssuerCode: String): Int {
        return when (cardIssuerCode.toInt()) {
            1001 -> R.drawable.kookmin_logo_small
            1002 -> R.drawable.samsungcard_logo_small
            1003 -> R.drawable.lottecard_logo_small
            1004 -> R.drawable.woori_logo_small
            1005 -> R.drawable.shinhan_logo_small
            1006 -> R.drawable.hyundaicard_logo_small
            1007 -> R.drawable.bccard_logo_small
            1008 -> R.drawable.nonghyup_logo_small
            1009 -> R.drawable.hana_logo_small
            1010 -> R.drawable.ibk_logo_small
            else -> R.drawable.sotory_logo
        }
    }
}
