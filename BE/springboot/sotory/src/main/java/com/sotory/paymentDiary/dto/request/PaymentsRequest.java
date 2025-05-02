package com.sotory.paymentDiary.dto.request;

public record PaymentsRequest(
        String merchantName,
        String categoryName,
        String transactionBalance,
        String transactionTime
) {}