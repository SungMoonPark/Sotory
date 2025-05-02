package com.sotory.card.entity;

import com.sotory.core.auditing.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "card")
public class Card extends CreateTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "card_id", columnDefinition = "UUID")
    private UUID cardId;

    @Column(nullable = false, length = 50)
    private UUID userId;

    @Column(nullable = false, length = 20)
    private String cardNo;

    @Column(nullable = false, length = 4)
    private String cardIssuerCode;

    @Column(nullable = false, length = 20)
    private String cardIssuerName;

    @Column(nullable = false, length = 100)
    private String cardName;

    @Column(nullable = false)
    private Boolean isDeleted = false;

}
