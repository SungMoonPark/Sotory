package com.sotory.paymentDiary.dto.response;

import com.sotory.paymentDiary.type.WeatherType;

import java.util.UUID;

public record GenerateDiaryCardResponse(
        Integer date,
        String summary,
        String imgSrc,
        UUID diaryCardId,
        WeatherType weather
) {
}