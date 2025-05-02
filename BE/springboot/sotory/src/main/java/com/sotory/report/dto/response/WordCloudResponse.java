package com.sotory.report.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordCloudResponse {
    private String code;
    private String message;
    private WordCloudData data;

    @Getter
    @Setter
    public static class WordCloudData {
        @JsonProperty("image_url")
        private String imageUrl;
    }
}