package com.sotory.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DailySpending {
    private String date; // yyyy-MM-dd
    private int amount;
}
