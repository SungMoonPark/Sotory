package com.sotory.paymentDiary.entity;

import com.sotory.core.auditing.BaseTimeEntity;
import com.sotory.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "payment_diary", indexes = {
        @Index(name = "idx_payment_diary_transaction_date", columnList = "transactionDate"),
})
public class PaymentDiary extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "paymentDiaryId", columnDefinition = "UUID")
    private UUID paymentDiaryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    // 일기 관련 필드
    @Column(columnDefinition = "TEXT", nullable = true)
    private String diary;

    // 결제 정보 관련 필드
    private UUID paymentId;
    private String merchantName; // 결제장소
    private String categoryName; // 카테고리
    private Integer transactionBalance; // 결제금액

    @Builder.Default
    private LocalDate transactionDate = LocalDate.now();
    private LocalTime transactionTime; // 결제시간

    @Builder.Default
    private boolean isDeleted = false; // 삭제여부

    @Builder.Default
    private boolean isUserAdded = false; // 사용자가 직접 추가한 소비내역인지

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateDiary(String diary) {
        this.diary = diary;
    }
    public void deleteDiary() {
        this.diary = null;
    }
}
