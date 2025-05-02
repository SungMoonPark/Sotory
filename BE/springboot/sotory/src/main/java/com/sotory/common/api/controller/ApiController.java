package com.sotory.common.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Card API", description = "카드 관련 API")
public class ApiController {

//    private final FinOpenApiService finOpenApiClient;

    // 카드 목록 조회는 테스트 완료 -> card 패키지로 이동

//    @Operation(summary = "1원 송금 인증", description = "계좌 확인을 위한 1원 송금을 실행합니다.")
//    @PostMapping("/auth/send")
//    public Mono<ResponseEntity<ApiResponseDto>> sendOneCent(
//            @RequestParam String userKey,
//            @RequestParam String accountNo,
//            @RequestParam String authText) {

//        ApiResponseDto response = finOpenApiClient.sendOneCentForAuth(userKey, accountNo, authText);
//        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "1원 송금 검증", description = "1원 송금 인증 코드를 검증합니다.")
//    @PostMapping("/auth/verify")
//    public Mono<ResponseEntity<ApiResponseDto>> verifyAuthCode(
//            @RequestParam String userKey,
//            @RequestParam String accountNo,
//            @RequestParam String authText,
//            @RequestParam String authCode) {
//        ApiResponseDto response = finOpenApiClient.verifyOneCentAuth(userKey, accountNo, authText, authCode);
//        return ResponseEntity.ok(response);
//    }

