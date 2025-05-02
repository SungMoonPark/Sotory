package com.sotory.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterCardRequest(
        @Schema(description = "카드 번호", example = "1001544819986361")
        String cardNo,

        @Schema(description = "카드사 번호", example = "1001")
        String cardIssuerCode,

        @Schema(description = "카드사 이름", example = "KB국민카드")
        String cardIssuerName,

        @Schema(description = "카드 이름", example = "SSAFY 스마일카드")
        String cardName

) { }
