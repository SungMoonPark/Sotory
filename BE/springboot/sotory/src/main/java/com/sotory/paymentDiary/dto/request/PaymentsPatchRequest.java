package com.sotory.paymentDiary.dto.request;

import com.sotory.paymentDiary.validation.NotBlankIfPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PaymentsPatchRequest(
        @Size(min = 1, max = 50, message = "결제 장소는 1자 이상 50자 이하로 입력해야 합니다.")
        @NotBlankIfPresent(message = "결제 장소는 공백없이 50자 이하여야 합니다.")
        String merchantName,

        @Size(min = 1, max = 20, message = "카테고리는 1자 이상 20자 이하로 입력해야 합니다.")
        @NotBlankIfPresent(message = "카테고리는 공백없이 20자 이하여야 합니다.")
        String categoryName,

        @Pattern(regexp = "^[1-9]\\d{2,9}$", message = "결제 금액은 100원 이상, 최대 999억 9999만 9999원까지 입력 가능합니다.")
        @NotBlankIfPresent(message="공백은 허용되지 않습니다")
        String transactionBalance,

        @NotBlankIfPresent(message="공백은 허용되지 않습니다")
        String transactionTime
) {
}
