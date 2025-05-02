package com.ssafy.sotory.domain.myroom

data class SpendingCategory(
    val categoryName: String,
    val amount: Int,
    val percentage: Double
)

data class FrequentMerchants(
    val rank: Int,
    val merchantName: String,
    val categoryName: String,
    val visits: Int,
    val amount: Int
)

data class DailySpending(
    val date: String,
    val amount: Int
)

data class MyRoomModel(
    val nickname: String,
    val budgetAmount: Int,
    val totalSpent: Int,
    val remainingBudget: Int,
    val monthlyLimit: Int,
    val budgetPercentage: Double,
    val categoryBreakdown: List<SpendingCategory>,
    val frequentMerchants: List<FrequentMerchants>,
    val dailySpending: List<DailySpending>,
    val keywordCloudUrl: String
)