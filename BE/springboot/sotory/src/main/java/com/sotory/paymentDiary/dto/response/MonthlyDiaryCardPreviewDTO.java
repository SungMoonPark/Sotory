package com.sotory.paymentDiary.dto.response;

import java.util.List;

public record MonthlyDiaryCardPreviewDTO (
    int month,
    int count,
    List<DiaryListResponseDTO> diaries
) {}
