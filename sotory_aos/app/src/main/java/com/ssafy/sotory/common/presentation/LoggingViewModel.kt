package com.ssafy.sotory.common.presentation

import android.util.Log
import androidx.lifecycle.ViewModel

// 1. ViewModel 확장 클래스 생성
abstract class LoggingViewModel : ViewModel() {
    private val tag = this.javaClass.simpleName

    init {
        Log.d(tag, "ViewModel initialized: ${this.javaClass.simpleName}")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "ViewModel cleared: ${this.javaClass.simpleName}")
    }

    // 데이터 변경을 로그로 출력하는 헬퍼 메소드
    protected fun <T> logStateChange(propertyName: String, oldValue: T, newValue: T) {
        Log.d(tag, "Data change in $propertyName: $oldValue -> $newValue")
    }
}
