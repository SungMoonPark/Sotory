package com.sotory.common.api.dto.response;

import java.util.List;

public record CardListResponseDto(
        List<CardInfo> data // 카드 정보 리스트
) {
    // 필요한 경우 여기에 추가 메서드를 정의할 수 있습니다

    // 내부 CardInfo 클래스도 record로 변환
    public record CardInfo(
            String cardNo,               // 카드 번호
            String cardIssuerCode,       // 카드 발급기관 코드
            String cardIssuerName,       // 카드 발급기관 이름
            String cardName             // 카드 이름
    ) {
        // 필요한 경우 여기에 추가 메서드를 정의할 수 있습니다
    }
}