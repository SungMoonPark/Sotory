package com.sotory.paymentDiary.service;

import com.sotory.paymentDiary.config.WeatherProperties;
import com.sotory.paymentDiary.dto.response.WeatherApiResponseDTO;
import com.sotory.paymentDiary.dto.response.WeatherCardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WeatherProperties weatherProperties;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/weather")
            .build();

    public WeatherCardDTO getWeatherByCoordinates(double latitude, double longitude) {
        log.info("Get weather by coordinates: {}, {}", latitude, longitude);

        String uri = UriComponentsBuilder.fromPath("")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherProperties.getKey())
                .queryParam("units", "metric")
                .queryParam("lang", "kr")
                .toUriString();

        log.info("Weather API 호출 URL: {}", uri);

        WeatherApiResponseDTO response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(WeatherApiResponseDTO.class)
                .doOnError(error -> log.error("Weather API 호출 실패", error))
                .block(); // 동기 방식으로 응답 대기

        if (response != null && response.weather() != null && !response.weather().isEmpty()) {
            String main = response.weather().get(0).main();
            String description = response.weather().get(0).description();
            log.info("Weather main: {}, description: {}", main, description);
            return new WeatherCardDTO(main, description);
        }

        return new WeatherCardDTO("DEFAULT", "기본");
    }
}
