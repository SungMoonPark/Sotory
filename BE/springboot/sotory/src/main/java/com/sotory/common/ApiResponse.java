package com.sotory.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sotory.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        HttpStatus code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "해당 작업이 성공적으로 완료되었습니다.", data);
    }

    // ErrorCode를 활용한 에러 응답 생성
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatusCode(), errorCode.getMessage(), null);
    }

    // 커스텀 메시지와 함께 ErrorCode 사용
    public static <T> ApiResponse<T> error(HttpStatus errorCode, String customMessage) {
        return new ApiResponse<>(errorCode, customMessage, null);
    }

}

