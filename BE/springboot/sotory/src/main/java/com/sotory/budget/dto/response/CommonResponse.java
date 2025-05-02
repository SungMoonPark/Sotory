package com.sotory.budget.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공통 응답 포맷 DTO
 */
public record CommonResponse<T>(
    
    @Schema(description = "응답 코드", example = "200")
    String code,

    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    String message,

    @Schema(description = "응답 데이터")
    T data

) { }
