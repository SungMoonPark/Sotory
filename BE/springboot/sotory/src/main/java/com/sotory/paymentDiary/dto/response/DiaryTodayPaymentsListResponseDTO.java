package com.sotory.paymentDiary.dto.response;

import java.util.List;

public record DiaryTodayPaymentsListResponseDTO(
        List<PaymentDiaryDTO> paymentDiaires,
        boolean isCardCreated
) { }
