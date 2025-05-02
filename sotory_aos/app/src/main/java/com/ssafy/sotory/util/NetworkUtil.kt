package com.ssafy.sotory.util

import kotlinx.serialization.json.Json

object NetworkUtil {
    const val BASE_URL = "https://j12a503.p.ssafy.io"
    val jsonBuilder = Json {
        ignoreUnknownKeys = true  // 알 수 없는 키 무시
        coerceInputValues = true  // null을 기본값으로 대체
        isLenient = true          // 유연한 파싱 허용
        explicitNulls = false     // null 필드 생략 허용
    }
    const val AUTHORIZATION = "Authorization"
    val WITH_TOKEN = listOf(
        "/auth/logout",
        "/users",
        "/cards",
        "/cards/owned",
        "/cards/connected",
        "/cards/"
    )
}