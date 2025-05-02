package com.sotory.paymentDiary.dto.response;

import java.util.List;

public record BadWordCheckResponse(
        boolean has_bad_words,
        List<String> bad_words
) {}
