package com.sotory.report.entity;

import com.sotory.core.auditing.BaseTimeEntity;
import com.sotory.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "wordclouds", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_yearmonth", columnNames = {"user_id", "yearMonth"})
})
public class WordCloud extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wordcloud_id", columnDefinition = "UUID")
    private UUID wordcloudId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 7)
    private String yearMonth;   // 예: 2025-04

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Integer diaryCount;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}