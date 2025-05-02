package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.myroom.BudgetResponse
import com.ssafy.sotory.data.dto.myroom.DailySpendingResponse
import com.ssafy.sotory.data.dto.myroom.FrequentMerchantsResponse
import com.ssafy.sotory.domain.myroom.DailySpending
import com.ssafy.sotory.data.dto.myroom.ReportResponse
import com.ssafy.sotory.data.dto.myroom.SpendingCategoryResponse
import com.ssafy.sotory.domain.myroom.BudgetModel
import com.ssafy.sotory.domain.myroom.FrequentMerchants
import com.ssafy.sotory.domain.myroom.MyRoomModel
import com.ssafy.sotory.domain.myroom.SpendingCategory

fun BudgetResponse.toDomain(): BudgetModel {
    return BudgetModel(
        budget = budget
    )
}

fun ReportResponse.toDomain(): MyRoomModel {
    return MyRoomModel(
        nickname = nickname,
        budgetAmount = budgetAmount,
        totalSpent = totalSpent,
        remainingBudget = remainingBudget,
        monthlyLimit = monthlyLimit,
        budgetPercentage = budgetPercentage,
        categoryBreakdown = categoryBreakdown.map { it.toDomain() },
        frequentMerchants = frequentMerchants.map { it.toDomain() },
        dailySpending = dailySpending.map { it.toDomain() },
        keywordCloudUrl = keywordCloudUrl

    )
}

fun SpendingCategoryResponse.toDomain(): SpendingCategory {
    return SpendingCategory(
        categoryName = categoryName,
        amount = amount,
        percentage = percentage
    )
}

fun FrequentMerchantsResponse.toDomain(): FrequentMerchants {
    return FrequentMerchants(
        rank = rank,
        merchantName = merchantName,
        categoryName = categoryName,
        visits = visits,
        amount = amount
    )
}

fun DailySpendingResponse.toDomain(): DailySpending {
    return DailySpending(
        date = date,
        amount = amount
    )
}