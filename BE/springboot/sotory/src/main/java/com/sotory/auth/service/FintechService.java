package com.sotory.auth.service;

import com.sotory.auth.dto.request.FintechRegisterRequestDTO;
import com.sotory.auth.dto.response.FintechRegisterResponseDTO;
import com.sotory.core.config.FintechProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class FintechService {
    private final FintechProvider fintechProvider;

    protected FintechRegisterResponseDTO generedUserFintechKey(FintechRegisterRequestDTO requestDto) {
        log.info("핀테크 회원가입 userID 조회: {}", requestDto.userId());
        log.info("url:{}",fintechProvider.getRegist());
        WebClient webClient = WebClient.builder()
                .baseUrl(fintechProvider.getRegist()) // baseUrl로 설정
                .build();

        Mono<FintechRegisterResponseDTO> response = webClient.post()
                .uri("")
                .header("Content-Type", "application/json")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(FintechRegisterResponseDTO.class);
        log.info("핀테크 회원가입 완료");
        return response.block();
    }
}
