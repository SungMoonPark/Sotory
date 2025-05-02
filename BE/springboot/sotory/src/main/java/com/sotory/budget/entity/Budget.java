package com.sotory.budget.entity;

import com.sotory.core.auditing.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate; // 수정된 부분!
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "budget_id", columnDefinition = "uuid") // 소문자 uuid
    private UUID budgetId; // 수정!

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private LocalDate month; // 수정! (java.time.LocalDate)

    @Column(nullable = false)
    private Integer budget;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
