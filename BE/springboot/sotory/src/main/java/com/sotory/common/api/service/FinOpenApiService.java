package com.sotory.common.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotory.common.api.dto.request.ApiRequestDto;
import com.sotory.common.api.dto.request.CommonHeader;
import com.sotory.common.api.dto.response.ApiResponseDto;
import com.sotory.common.api.dto.response.CardListResponseDto;
import com.sotory.core.config.WebClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinOpenApiService {

    private final WebClientProvider webClientProvider;


    @Value("${fintech.api.key}")
    private String apiKey;

    @Value("${fintech.base.url}")
    private String baseUrl;

    /**
     * 카드 목록 조회 API
     *
     * @param userKey 사용자 식별 키
     * @return 카드 목록 정보
     */
    //https://finopenapi.ssafy.io/ssafy/api/v1/edu/creditCard/inquireSignUpCreditCardList 카드목록조회
    public CardListResponseDto getCardOwnedList(String userKey) {
        log.info("카드 목록 조회 API 호출 시작 - userKey: {}", userKey);

        // 헤더 객체 생성 및 API 요청
        CommonHeader cardListHeader = CommonHeader.createHeader(
                "inquireSignUpCreditCardList",
                apiKey,
                userKey
        );
        ApiRequestDto request = ApiRequestDto.fromCommonHeader(cardListHeader);
        ApiResponseDto apiResponse = callExternalApi("/creditCard/inquireSignUpCreditCardList", request).block();

        // null 체크 추가
        if (apiResponse == null) {
            return null;
        }

        // 헤더 null 체크
        Map<String, Object> header = apiResponse.Header();  // record는 메서드 형태로 getter 제공
        if (header == null) {
            return null;
        }

        // 안전하게 값 추출
        String responseCode = safeGetString(header, "responseCode");
        String responseMessage = safeGetString(header, "responseMessage");


        if(responseCode == null || !responseCode.equals("H0000")){
            // 실패한 경우 - 원본 에러 코드와 메시지 그대로 전달
            String code = responseCode != null ? responseCode : "E9999";
            String message = responseMessage != null ? responseMessage : "알 수 없는 오류";

            log.warn("카드 목록 조회 API 오류 응답 - 코드: {}, 메시지: {}", code, message);
            return null;
        }

        // 결과 변환 및 반환
//        String code = "200";
//        String message = responseMessage;

        // 카드 정보 변환 - 더 간결하게 처리
        List<CardListResponseDto.CardInfo> cardInfoList = new ArrayList<>();

        List<Map<String, Object>> recList = apiResponse.REC();  // record는 메서드 형태로 getter 제공
        if (recList != null) {
            for (Map<String, Object> cardData : recList) {
                if (cardData != null) {
                    // 각 카드 정보를 record 생성자를 통해 생성
                    CardListResponseDto.CardInfo cardInfo = new CardListResponseDto.CardInfo(
                            safeGetString(cardData, "cardNo"),
                            safeGetString(cardData, "cardIssuerCode"),
                            safeGetString(cardData, "cardIssuerName"),
                            safeGetString(cardData, "cardName")
                    );

                    cardInfoList.add(cardInfo);
                }
            }
        }

        log.info("카드 목록 조회 API 호출 완료 - 카드 개수: {}", cardInfoList.size());
        return new CardListResponseDto(cardInfoList);
    }

    // 안전하게 String 값을 가져오는 헬퍼 메서드
    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 외부 API 호출 공통 메서드
     *
     * @param uri     API 엔드포인트 URI
     * @param request API 요청 DTO
     * @return API 응답 DTO
     */
    private Mono<ApiResponseDto> callExternalApi(String uri, ApiRequestDto request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(request);
            log.info("🚀 API 요청 - URI: {}, 페이로드:\n{}", uri, requestJson);
        } catch (Exception e) {
            log.error("요청 페이로드 로깅 중 오류", e);
        }

        ObjectMapper mapper = new ObjectMapper();

        return webClientProvider.fintechClient().post()  // ✅ 여기 바뀐 부분!
                .uri(baseUrl + uri)
                .bodyValue(request)
                .exchangeToMono(response -> response.bodyToMono(String.class).map(body -> {
                    if (response.statusCode().isError()) {
                        log.error("🚨 API 에러 응답 - 상태코드: {}, 응답 본문:\n{}", response.statusCode(), body);
                    } else {
                        log.info("📥 API 응답 - 응답 본문:\n{}", body);
                    }

                    try {
                        return mapper.readValue(body, ApiResponseDto.class);
                    } catch (Exception e) {
                        log.warn("API 응답 파싱 실패, 기본 응답 생성: {}", e.getMessage());
                        Map<String, Object> header = new HashMap<>();
                        header.put("responseCode", "E9999");
                        header.put("responseMessage", "응답 파싱 오류: " + e.getMessage());
                        return new ApiResponseDto(header, null);
                    }
                }))
                .onErrorResume(e -> {
                    log.error("🚨 API 호출 중 예외 발생", e);
                    Map<String, Object> header = new HashMap<>();
                    header.put("responseCode", "E9998");
                    header.put("responseMessage", "API 호출 오류: " + e.getMessage());
                    return Mono.just(new ApiResponseDto(header, null));
                });
    }
}

