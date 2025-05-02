package com.ssafy.sotory.data.dto.creditcard

import kotlinx.serialization.Serializable

@Serializable
data class CardRemoveRequest(
    val cardId: Long
)