package com.sotory.paymentDiary.entity;

import com.sotory.paymentDiary.type.WeatherType;
import com.sotory.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "diary_card", indexes = {
        @Index(name = "idx_diary_created_at", columnList = "createdAt")
})
public class DiaryCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "diary_card_id", columnDefinition = "UUID")
    private UUID diaryCardId;


    private String summary;
    private String imgSrc;
    private LocalDate createdAt;
    @Enumerated(EnumType.STRING)
    private WeatherType Weather;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @PrePersist
    public void prePersist() {
        if (this.Weather == null) {
            this.Weather = WeatherType.DEFAULT;
        }
    }
}
