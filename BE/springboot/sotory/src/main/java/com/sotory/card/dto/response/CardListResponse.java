package com.sotory.card.dto.response;

import java.util.List;
import java.util.UUID;

public record CardListResponse(
        UUID cardId,
        String cardNo,
        String cardIssuerCode,
        String cardIssuerName,
        String cardName
) { }
