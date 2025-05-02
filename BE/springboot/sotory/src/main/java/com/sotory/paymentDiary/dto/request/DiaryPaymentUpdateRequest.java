package com.sotory.paymentDiary.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
public class DiaryPaymentUpdateRequest {
    @Size(max = 500, message = "일기는 최대 500자까지 작성할 수 있습니다.")
    private String diary;
}
