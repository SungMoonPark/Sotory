package com.sotory.budget.service;

import com.sotory.budget.entity.Budget;
import com.sotory.budget.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public Optional<Budget> getBudget(UUID userId, String monthStr) {
        YearMonth yearMonth = parseMonthString(monthStr);
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();

        return budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);
    }

    @Transactional
    public Budget updateBudget(UUID userId, String monthStr, int budgetAmount) {
        YearMonth yearMonth = parseMonthString(monthStr);
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();

        Optional<Budget> optionalBudget = budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);

        if (optionalBudget.isPresent()) {
            Budget existingBudget = optionalBudget.get();
            existingBudget.setBudget(budgetAmount);
            return budgetRepository.save(existingBudget);
        } else {
            Budget newBudget = new Budget();
            newBudget.setBudgetId(UUID.randomUUID());
            newBudget.setUserId(userId);
            newBudget.setMonth(yearMonth.atDay(1)); // YearMonth → LocalDate 변환
            newBudget.setBudget(budgetAmount);
            return budgetRepository.save(newBudget);
        }
    }

    private YearMonth parseMonthString(String monthStr) {
        try {
            return YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e1) {
            try {
                LocalDate date = LocalDate.parse(monthStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return YearMonth.from(date);
            } catch (Exception e2) {
                throw new IllegalArgumentException("잘못된 날짜 형식입니다. 'yyyy-MM' 또는 'yyyy-MM-dd' 형식이어야 합니다.");
            }
        }
    }
}
