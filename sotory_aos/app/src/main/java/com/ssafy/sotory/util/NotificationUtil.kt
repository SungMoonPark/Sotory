package com.ssafy.sotory.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ssafy.sotory.MainActivity


object NotificationUtil {

    private const val CHANNEL_ID = "diary_generation_channel"
    private const val CHANNEL_NAME = "일기 생성 알림"
    private const val CHANNEL_DESCRIPTION = "일기 생성 과정 및 완료 알림"

    /**
     * 알림 채널 생성 (Android O 이상)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 알림 권한이 있는지 확인
     */
    fun hasNotificationPermission(context: Context): Boolean {
        // Android 13 (API 33) 이상에서만 런타임 권한 체크가 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 이하에서는 별도 권한 체크 불필요
            true
        }
    }

    /**
     * 알림 권한 요청 (액티비티에서 호출)
     */
    fun requestNotificationPermission(
        activity: MainActivity,
        permissionLauncher: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission(activity)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * 권한이 거부되었을 때 설정 화면으로 이동
     */
    fun openNotificationSettings(context: Context) {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // 채널 ID 조회용 함수
    fun getChannelId(): String = CHANNEL_ID
}