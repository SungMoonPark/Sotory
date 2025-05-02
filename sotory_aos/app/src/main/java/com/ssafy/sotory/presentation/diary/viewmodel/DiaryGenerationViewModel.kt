package com.ssafy.sotory.presentation.diary.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.sotory.MainActivity
import com.ssafy.sotory.R
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.domain.diary.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// 일기 생성 상태를 나타내는 sealed class
sealed class GenerationState {
    data object Idle : GenerationState()
    data object Loading : GenerationState()
    data class Success(val diary: DiaryModel) : GenerationState()
    data class Error(val message: String) : GenerationState()
}


// 네비게이션 이벤트를 나타내는 sealed class
sealed class NavigationEvent {
    data object ToLoadingScreen : NavigationEvent()
    data class ToCompletionScreen(val diary: DiaryModel) : NavigationEvent()
    data class ToErrorScreen(val errorMessage: String) : NavigationEvent()
}

@HiltViewModel
class DiaryGenerationViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    // 작업 ID를 저장하는 변수
    private var currentJobId: String? = null

    // 채널 ID 상수
    companion object {
        private const val CHANNEL_ID = "diary_generation_channel"
        private const val NOTIFICATION_ID = 1001
    }

    init {
        Log.d("DiaryGenerationViewModel", "init called")
        // 알림 채널 생성
        createNotificationChannel()
    }

    fun generateDiary() {
        // 작업 ID 생성
        currentJobId = UUID.randomUUID().toString()
        val jobId = currentJobId ?: return

        // 상태 업데이트
        _generationState.value = GenerationState.Loading

        // 먼저 대기 화면으로 네비게이션
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.ToLoadingScreen)
        }

        // 백그라운드에서 API 호출
        viewModelScope.launch {
            try {
//                // 실제 API 호출 (가정)
                diaryRepository.postDiary().collect {
                    Log.d("DiaryGenerationViewModel", "API 호출 결과: $it")
                    _navigationEvent.emit(NavigationEvent.ToCompletionScreen(it))
                    // API 호출 성공
                    if (jobId == currentJobId) {  // 현재 작업이 여전히 활성 상태인지 확인
                        _generationState.value = GenerationState.Success(it)

                        // 작업 완료 알림 표시
                        showCompletionNotification()

                        // 완료 화면으로 이동 이벤트 발생
                    }
                }
            } catch (e: Exception) {
                Log.d("DiaryGenerationViewModel", "API 호출 실패: ${e.message}")
                if (jobId == currentJobId) {
                    _generationState.value =
                        GenerationState.Error(e.message ?: "Unknown error occurred")
                    _navigationEvent.emit(
                        NavigationEvent.ToErrorScreen(
                            e.message ?: "Unknown error occurred"
                        )
                    )
                }
            }
        }
    }

    // 현재 작업 취소
    fun cancelGeneration() {
        currentJobId = null
        _generationState.value = GenerationState.Idle
    }

    // 알림 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Diary Generation"
            val descriptionText = "Notifications for diary generation"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 작업 완료 알림 표시
    private fun showCompletionNotification() {
        // 중요: 알림이 표시된 후 네비게이션 이벤트 emit
//        viewModelScope.launch {
//            _navigationEvent.emit(NavigationEvent.ToCompletionScreen)
//        }
        val context = getApplication<Application>()

        // 딥링크로 결과 화면으로 바로 이동하는 Intent 생성
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("sotory://diary/completion")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.sotory_logo) // 실제 앱의 알림 아이콘으로 바꿔주세요
            .setContentTitle("일기 생성 완료").setContentText("일기가 성공적으로 생성되었습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                // Android 13 이상에서는 알림 권한 체크가 필요합니다
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // 알림 권한이 없는 경우 처리
            }
        }
    }
}