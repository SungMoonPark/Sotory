package com.sotory.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sotory.report.dto.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String nickname;
    private int budgetAmount;          // 예산 금액
    private int totalSpent;            // 총 지출 금액
    private int remainingBudget;       // 남은 예산 (예산 - 지출)
    private int monthlyLimit;          // 월간 한도 (budgetAmount와 같은 값)
    private double budgetPercentage;   // 예산 대비 사용률 (%) (ex. 73.5%)

    private List<CategoryBreakdown> categoryBreakdown; // 카테고리별 사용 내역
    private List<FrequentMerchants> frequentMerchants; // 자주 가는 상점
    private List<DailySpending> dailySpending;         // 일별 지출
    private String keywordCloudUrl;                       // 키워드 클라우드 주소 

}
