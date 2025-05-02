package com.sotory.paymentDiary.repository;

import com.sotory.paymentDiary.entity.DiaryCard;
import com.sotory.paymentDiary.entity.PaymentDiary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;


@Repository
public interface PaymentDiaryRepository extends JpaRepository<PaymentDiary, UUID> {

    @Query("""
    SELECT pd FROM PaymentDiary pd
    JOIN FETCH pd.user u
    WHERE u.userId = :userId AND pd.transactionDate = :transactionDate
    AND pd.isDeleted = false
    """)
    List<PaymentDiary>  findAllWithUserByUseridAndTransactionDate(@Param("userId") UUID userId,
                                                                 @Param("transactionDate") LocalDate transactionDate);

    List<PaymentDiary> findAllByUser_UserIdAndTransactionDateAndIsDeletedFalseAndPaymentIdNotIn(
            UUID userId,
            LocalDate transactionDate,
            Collection<UUID> excludedPaymentIds
    );

    Optional<PaymentDiary> findByPaymentIdAndIsDeletedFalse(UUID paymentId);

    Optional<PaymentDiary> findByPaymentDiaryIdAndIsDeletedFalse(UUID paymentDiaryId);


    @Query("""
    	    SELECT COALESCE(SUM(pd.transactionBalance), 0) 
    	    FROM PaymentDiary pd
    	    WHERE pd.user.userId = :userId
    	    AND pd.transactionDate BETWEEN :startDate AND :endDate
    	    AND pd.isDeleted = false
    	""")
    	int sumTransactionBalanceByUserAndMonth(@Param("userId") UUID userId,
    	                                        @Param("startDate") LocalDate startDate,
    	                                        @Param("endDate") LocalDate endDate);

    List<PaymentDiary> findAllByUser_UserIdAndTransactionDateBetweenAndIsDeletedFalse(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<PaymentDiary> findByUser_UserIdAndTransactionDate(UUID userId, LocalDate today);
}
