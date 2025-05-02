package com.ssafy.sotory.domain.payment

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PaymentModel(
    val paymentDiaryId: String,
    val categoryName: String,
    val diary: String?,
    val merchantName: String,
    val paymentId: String,
    val transactionBalance: String,
    val transactionTime: String,
    val isUserAdded: Boolean,
)


object PaymentType : NavType<PaymentModel?>(true) {
    override fun get(bundle: Bundle, key: String): PaymentModel? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): PaymentModel? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: PaymentModel?) {
        bundle.putString(key, value?.let { Json.encodeToString(it) })
    }

    override fun serializeAsValue(value: PaymentModel?): String {
        if (value == null) return ""
        return Json.encodeToString(PaymentModel.serializer(), value)
    }
}

