package com.sotory.auth.service;

import com.sotory.auth.dto.response.KakaoUserInfoResponseDTO;
import com.sotory.common.exception.CustomException;
import com.sotory.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaoOAuthService {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final WebClient.Builder webClientBuilder;

    public KakaoOAuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // 카카오 액세스 토큰을 이용해 사용자 정보를 가져옴
    public KakaoUserInfoResponseDTO getKakaoUserInfo(String kakaoAccessToken) {
        // WebClient 사용해 카카오 API 호출
        return webClientBuilder.build()
                .get()
                .uri(KAKAO_API_URL)
                .header("Authorization", "Bearer " + kakaoAccessToken)  // Authorization 헤더 추가
                .retrieve()  // HTTP 요청 실행
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new CustomException(ErrorCode.INVALID_KAKAO_TOKEN))
                )
                .bodyToMono(KakaoUserInfoResponseDTO.class)  // 응답을 KakaoUserInfo 객체로 변환
                .block();  // 비동기 호출이므로 block()을 사용해 동기적으로 결과를 기다림
    }
}
