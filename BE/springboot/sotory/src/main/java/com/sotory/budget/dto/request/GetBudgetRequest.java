package com.sotory.budget.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth; // ✅ 연/월만 다룰 때 사용하는 타입

public record GetBudgetRequest(
        @Schema(description = "연월", example = "2025-04") // 수정
        YearMonth month
) { }