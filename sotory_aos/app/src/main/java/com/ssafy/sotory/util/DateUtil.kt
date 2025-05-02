package com.ssafy.sotory.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun formatDateTime(dateTimeString: String): String {
// 1. 입력 문자열을 파싱할 포맷
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

// 2. 출력 문자열 포맷
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
    // 3. 파싱 → 포맷
    val parsedDateTime = LocalDateTime.parse(dateTimeString, inputFormatter)
    return parsedDateTime.format(outputFormatter)
}

fun formatDate(dateString: String): String {
// 1. 입력 문자열을 파싱할 포맷
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// 2. 출력 문자열 포맷
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    // 3. 파싱 → 포맷
    val parsedDateTime = LocalDate.parse(dateString, inputFormatter)
    return parsedDateTime.format(outputFormatter)
}