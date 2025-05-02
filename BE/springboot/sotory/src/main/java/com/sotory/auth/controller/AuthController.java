package com.sotory.auth.controller;

import com.sotory.auth.dto.response.AuthResponseDTO;
import com.sotory.auth.dto.request.KakaoLoginRequestDTO;
import com.sotory.auth.dto.request.LogoutRequestDTO;
import com.sotory.auth.dto.TokenUpdateDTO;
import com.sotory.auth.service.AuthService;
import com.sotory.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequestDTO request) {
        AuthResponseDTO response = authService.kakaoLogin(request.getAccessToken());
        Map<String, String> tokenMap = Map.of(
                "accessToken", response.getAccessToken(),
                "refreshToken", response.getRefreshToken()
        );

        return ResponseEntity.ok(ApiResponse.success(tokenMap));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenUpdateDTO request ) {
        AuthResponseDTO response = authService.reissueToken(request.getRefreshToken());
        Map<String, String> tokenMap = Map.of(
                "accessToken", response.getAccessToken(),
                "refreshToken", response.getRefreshToken()
        );

        return ResponseEntity.ok(ApiResponse.success(tokenMap));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken, @RequestBody LogoutRequestDTO request) {
        log.info("로그아웃 - 컨트롤러 시작");
        authService.logout(extractToken(accessToken), request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Bearer 헤더에서 실제 토큰 추출
     */
    private String extractToken(String header) {
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }


}
