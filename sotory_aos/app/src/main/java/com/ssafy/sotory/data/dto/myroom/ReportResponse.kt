package com.ssafy.sotory.data.dto.myroom

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val nickname: String,
    val budgetAmount: Int,
    val totalSpent: Int,
    val remainingBudget: Int,
    val monthlyLimit: Int,
    val budgetPercentage: Double,
    val categoryBreakdown: List<SpendingCategoryResponse>,
    val frequentMerchants: List<FrequentMerchantsResponse>,
    val dailySpending: List<DailySpendingResponse>,
    val keywordCloudUrl: String
)

@Serializable
data class SpendingCategoryResponse(
    val categoryName: String,
    val amount: Int,
    val percentage: Double
)

@Serializable
data class FrequentMerchantsResponse(
    val rank: Int,
    val merchantName: String,
    val categoryName: String,
    val visits: Int,
    val amount: Int
)

@Serializable
data class DailySpendingResponse(
    val date: String,
    val amount: Int
)