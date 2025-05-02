package com.sotory.budget.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BudgetResponse(
        @Schema(description = "예산", example = "1000000")
        Integer budget
) { }