package com.sotory.common.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ApiResponseDto(
        @JsonProperty("Header") Map<String, Object> Header,
        @JsonProperty("REC") List<Map<String, Object>> REC
) {
    // 필요한 경우 추가 메서드를 여기에 작성할 수 있습니다
}