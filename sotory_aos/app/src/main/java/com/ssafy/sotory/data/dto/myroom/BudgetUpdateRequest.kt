package com.ssafy.sotory.data.dto.myroom

import kotlinx.serialization.Serializable

@Serializable
data class BudgetUpdateRequest(
    val budget: Int,
    val month: String
)
