package com.sotory.report.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record WordCloudRequest(
    List<String> texts,

    @JsonProperty("user_id")
    UUID userId,

    @JsonProperty("background_color")
    String backgroundColor,

    @JsonProperty("max_words")
    Integer maxWords,

    @JsonProperty("year_month")
    String yearMonth  // ✅ 추가된 필드
) {
}
