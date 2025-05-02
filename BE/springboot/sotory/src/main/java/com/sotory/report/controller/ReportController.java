package com.sotory.report.controller;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.common.ApiResponse;
import com.sotory.report.dto.response.ReportResponse;
import com.sotory.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "사용자 월간 소비 리포트 조회", description = "사용자의 월간 소비 리포트를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getReport(
            @CurrentUserInfo CurrentUser user,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        log.info("📌 [GET] /api/reports 호출 - userId: {}, year: {}, month: {}", user.getUserId(), year, month);

        try {
            if (year == null || month == null) {
                LocalDate now = LocalDate.now();
                year = now.getYear();
                month = now.getMonthValue();
                log.info("🗓️ year 또는 month 파라미터 없음, 기본값 설정: year={}, month={}", year, month);
            }

            if (month < 1 || month > 12) {
                log.warn("🚨 잘못된 month 값 입력: {}", month);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "월(month) 값은 1부터 12 사이여야 합니다."));
            }

            String monthStr = String.format("%04d-%02d", year, month);
            ReportResponse reportData = reportService.getMonthlyReport(user.getUserId(), monthStr);

            log.info("✅ 리포트 생성 성공 - userId: {}, month: {}", user.getUserId(), monthStr);

            return ResponseEntity
                    .ok(ApiResponse.success(reportData));

        } catch (Exception e) {
            log.error("❌ 리포트 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "리포트 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
