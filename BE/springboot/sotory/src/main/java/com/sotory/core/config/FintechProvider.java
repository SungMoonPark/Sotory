package com.sotory.core.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "fintech")
public class FintechProvider {
    private String key;
    private String regist;
    private String userKey;
}