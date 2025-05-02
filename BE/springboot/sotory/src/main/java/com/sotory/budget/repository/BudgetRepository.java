package com.sotory.budget.repository;

import com.sotory.budget.entity.Budget;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends CrudRepository<Budget, UUID> {

    @Query("""
            SELECT b FROM Budget b
            WHERE b.userId = :userId
              AND EXTRACT(YEAR FROM b.month) = :year
              AND EXTRACT(MONTH FROM b.month) = :month
        """)
        Optional<Budget> findByUserIdAndYearAndMonth(
                @Param("userId") UUID userId,
                @Param("year") int year,
                @Param("month") int month
        );

}