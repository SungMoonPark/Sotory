package com.sotory.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryBreakdown {
    private String categoryName;
    private int amount;
    private double percentage;
}
