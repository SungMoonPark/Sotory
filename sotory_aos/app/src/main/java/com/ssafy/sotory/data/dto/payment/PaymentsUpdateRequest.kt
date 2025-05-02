package com.ssafy.sotory.data.dto.payment

import kotlinx.serialization.Serializable

/*
{
"merchantName"?: "String",     // 결제장소
"categoryName"?: "String",     // 카테고리
"transactionBalance"?: "number", // 결제금액
"transactionTime"?: "String"   // 결제시간
}
 */
@Serializable
data class PaymentsUpdateRequest(
    val merchantName: String?,
    val categoryName: String?,
    val transactionBalance: String?,
    val transactionTime: String?,
)