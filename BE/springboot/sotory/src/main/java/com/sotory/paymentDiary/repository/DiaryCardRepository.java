package com.sotory.paymentDiary.repository;

import com.sotory.paymentDiary.entity.DiaryCard;
import com.sotory.paymentDiary.entity.PaymentDiary;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaryCardRepository extends JpaRepository<DiaryCard, UUID> {
    List<DiaryCard> findAllByUser_UserIdAndCreatedAtBetween(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            Sort sort
    );
    Optional<DiaryCard> findByUser_UserIdAndCreatedAt(UUID userId, LocalDate createdAt);

    List<DiaryCardRepository> findByUser_UserIdAndCreatedAtBetween(UUID userId, LocalDate localDate, LocalDateTime localDateTime);

    List<PaymentDiary> findPaymentDiaryByUser_UserIdAndCreatedAt(UUID userId, LocalDate today);
}
