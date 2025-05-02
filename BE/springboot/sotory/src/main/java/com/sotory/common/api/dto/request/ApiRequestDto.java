package com.sotory.common.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestDto {
    @JsonProperty("Header")
    private Map<String, Object> Header;

    @JsonProperty("Body")
    private Map<String, Object> Body;

    // CommonHeader로부터 ApiRequestDto 생성하는 정적 메서드
    public static ApiRequestDto fromCommonHeader(CommonHeader header) {
        return ApiRequestDto.builder()
                .Header(convertHeaderToMap(header))
                .Body(new HashMap<>())
                .build();
    }

    // 바디 파라미터 추가 메서드
    public ApiRequestDto addBodyParam(String key, Object value) {
        if (this.Body == null) {
            this.Body = new HashMap<>();
        }
        this.Body.put(key, value);
        return this;
    }

    // CommonHeader를 Map으로 변환하는 유틸리티 메서드
    private static Map<String, Object> convertHeaderToMap(CommonHeader header) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("apiName", header.apiName());
        headerMap.put("transmissionDate", header.transmissionDate());
        headerMap.put("transmissionTime", header.transmissionTime());
        headerMap.put("institutionCode", header.institutionCode());
        headerMap.put("fintechAppNo", header.fintechAppNo());
        headerMap.put("apiServiceCode", header.apiServiceCode());
        headerMap.put("institutionTransactionUniqueNo", header.institutionTransactionUniqueNo());
        headerMap.put("apiKey", header.apiKey());
        headerMap.put("userKey", header.userKey());
        return headerMap;
    }

}


