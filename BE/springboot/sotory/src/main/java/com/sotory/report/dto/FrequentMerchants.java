package com.sotory.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FrequentMerchants {
    private int rank;  // 1위, 2위, 3위
    private String merchantName;
    private String categoryName;
    private int visits;
    private int amount;
}
