package com.sotory.paymentDiary.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sotory.paymentDiary.entity.PaymentDiary;
import jakarta.persistence.Column;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDiaryDTO {
    private UUID paymentDiaryId;
    private UUID paymentId;              // 결제내역 ID
    private String merchantName;           // 결제장소
    private String categoryName;           // 카테고리
    private long transactionBalance;     // 결제금액
    private String transactionTime;        // 결제시간
    private String diary;
    // 일기 (nullable)

    @JsonProperty("isUserAdded")
    @Column(name = "is_user_added")
    private boolean userAdded;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static PaymentDiaryDTO fromEntity(PaymentDiary entity) {
        String formattedDateTime = null;

        if (entity.getTransactionDate() != null && entity.getTransactionTime() != null) {
            formattedDateTime = entity.getTransactionDate().atTime(entity.getTransactionTime()).format(FORMATTER);
        }

        return PaymentDiaryDTO.builder()
                .paymentDiaryId(entity.getPaymentDiaryId())
                .paymentId(entity.getPaymentId())
                .merchantName(entity.getMerchantName())
                .categoryName(entity.getCategoryName())
                .transactionBalance(entity.getTransactionBalance())
                .transactionTime(formattedDateTime)
                .diary(entity.getDiary())
                .userAdded(entity.isUserAdded())
                .build();
    }
}
