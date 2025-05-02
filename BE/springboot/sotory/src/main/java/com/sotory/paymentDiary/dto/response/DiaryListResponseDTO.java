package com.sotory.paymentDiary.dto.response;

import com.sotory.paymentDiary.type.WeatherType;
import com.sotory.paymentDiary.entity.DiaryCard;

public record DiaryListResponseDTO(
			String diaryCardId,
            String imgSrc,
            int date,
            String summary,
			WeatherType weather
){
	public static DiaryListResponseDTO fromEntity(DiaryCard Entity) {
		return new DiaryListResponseDTO(
				Entity.getDiaryCardId().toString(),
				Entity.getImgSrc(),
				Entity.getCreatedAt().getDayOfMonth(),
				Entity.getSummary(),
				Entity.getWeather() != null ? Entity.getWeather() : WeatherType.DEFAULT
		);
	}
}
