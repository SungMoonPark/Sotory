package com.sotory.paymentDiary.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    /**
     * 다양한 형식의 날짜 문자열을 ISO 형식(yyyy-MM-dd)으로 변환
     * null이나 빈 문자열이면 현재 날짜 반환
     */
    public static String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        try {
            // 하이픈이 없고 8자리면 yyyyMMdd 형식으로 간주
            if (dateStr.length() == 8 && !dateStr.contains("-")) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate date = LocalDate.parse(dateStr, inputFormatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }

            // 이미 ISO 형식이거나 다른 형식인 경우 파싱 시도
            LocalDate date = LocalDate.parse(dateStr);
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 현재 날짜 반환
            return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    /**
     * 다양한 형식의 시간 문자열을 ISO 형식(HH:mm:ss)으로 변환
     * null이나 빈 문자열이면 현재 시간 반환
     */
    public static String formatTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        }

        try {
            // 콜론이 없고 4자리 또는 6자리면 HHmm 또는 HHmmss 형식으로 간주
            if (!timeStr.contains(":")) {
                DateTimeFormatter inputFormatter;
                if (timeStr.length() == 4) {
                    inputFormatter = DateTimeFormatter.ofPattern("HHmm");
                } else if (timeStr.length() == 6) {
                    inputFormatter = DateTimeFormatter.ofPattern("HHmmss");
                } else {
                    // 형식이 맞지 않으면 현재 시간 반환
                    return LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
                }

                LocalTime time = LocalTime.parse(timeStr, inputFormatter);
                return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
            }

            // 이미 HH:mm(:ss) 형식인 경우
            LocalTime time;
            if (timeStr.length() <= 5) { // HH:mm
                time = LocalTime.parse(timeStr + ":00"); // 초 추가
            } else {
                time = LocalTime.parse(timeStr);
            }
            return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 현재 시간 반환
            return LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        }
    }
}