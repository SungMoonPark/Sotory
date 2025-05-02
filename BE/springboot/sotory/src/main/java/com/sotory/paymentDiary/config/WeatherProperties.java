package com.sotory.paymentDiary.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weather.api")
@Getter
@Setter
public class WeatherProperties {
    private String key;
}