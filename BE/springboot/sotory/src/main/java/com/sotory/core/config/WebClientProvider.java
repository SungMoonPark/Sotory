package com.sotory.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebClientProvider {

    private final WebClient.Builder builder;

    @Value("${fintech.base.url}")
    private String fintechBaseUrl;

    @Value("${fastapi.base.url}")
    private String fastapiBaseUrl;

    public WebClient fintechClient() {
        return builder
                .baseUrl(fintechBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public WebClient fastapiClient() {
        return builder
                .baseUrl(fastapiBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
