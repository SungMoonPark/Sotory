package com.sotory.budget.controller;

import com.sotory.budget.service.BudgetService;
import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.budget.entity.Budget;
import com.sotory.budget.dto.request.*;
import com.sotory.budget.dto.request.UpdateBudgetRequest;

import com.sotory.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/budgets")
@AllArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "사용자 예산 조회", description = "사용자의 예산 설정을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getBudget(
            @CurrentUserInfo CurrentUser user,
            @RequestParam(value = "month", required = false) String monthStr
    ) {
        log.info("📌 [GET] /api/budgets 호출 - userId: {}, month: {}", user.getUserId(), monthStr);

        if (monthStr == null || monthStr.isEmpty()) {
            monthStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            log.info("🗓️ month 파라미터 없음, 기본값 설정: {}", monthStr);
        }

        Optional<Budget> optionalBudget = budgetService.getBudget(user.getUserId(), monthStr);

        if (optionalBudget.isEmpty()) {
            log.warn("❗ Budget 없음 - userId: {}, month: {}", user.getUserId(), monthStr);
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        Budget budget = optionalBudget.get();
        log.info("✅ Budget 조회 성공 - userId: {}, month: {}, budget: {}", user.getUserId(), monthStr, budget.getBudget());

        return ResponseEntity.ok(ApiResponse.success(new BudgetResponse(budget.getBudget())));
    }

    @Operation(summary = "사용자 예산 수정", description = "사용자의 예산 설정을 수정합니다.")
    @PatchMapping
    public ResponseEntity<?> updateBudget(
            @CurrentUserInfo CurrentUser user,
            @RequestBody UpdateBudgetRequest request
    ) {
        String monthStr = request.month();
        Integer budgetAmount = request.budget();

        log.info("📌 [PATCH] /api/budgets 호출 - userId: {}, budgetAmount: {}, month: {}", user.getUserId(), budgetAmount, monthStr);

        if (monthStr == null || monthStr.isEmpty()) {
            monthStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            log.info("🗓️ month 파라미터 없음, 기본값 설정: {}", monthStr);
        }

        Budget budget = budgetService.updateBudget(user.getUserId(), monthStr, budgetAmount);

        log.info("✅ Budget 수정 성공 - userId: {}, month: {}, budget: {}", user.getUserId(), monthStr, budget.getBudget());

        return ResponseEntity.ok(ApiResponse.success(new BudgetResponse(budget.getBudget())));
    }

    @Data
    @AllArgsConstructor
    public static class BudgetResponse {
        private int budget;
    }
}
