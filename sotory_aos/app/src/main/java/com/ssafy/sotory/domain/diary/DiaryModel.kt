package com.ssafy.sotory.domain.diary

import android.os.Bundle
import androidx.navigation.NavType
import com.ssafy.sotory.data.dto.diary.Weather
import com.ssafy.sotory.domain.payment.PaymentModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface BaseDiaryModel

class EmptyDiaryModel : BaseDiaryModel {}

@Serializable
    data class DiaryModel(
    val date: Int,
    val diaryCardId: String,
    val imgSrc: String,
    val summary: String,
    val weather: Weather,
) : BaseDiaryModel


object DiaryType : NavType<DiaryModel>(false) {
    override fun get(bundle: Bundle, key: String): DiaryModel? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): DiaryModel {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: DiaryModel) {
        bundle.putString(key, value.let { Json.encodeToString(it) })
    }

    override fun serializeAsValue(value: DiaryModel): String {
        return Json.encodeToString(DiaryModel.serializer(), value)
    }
}


data class OtherDiaryModel(
    val imgSrc: MutableList<String>,
)