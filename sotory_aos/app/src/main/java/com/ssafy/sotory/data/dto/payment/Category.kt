package com.ssafy.sotory.data.dto.payment

import com.ssafy.sotory.R

/**
 * 소비 카테고리를 정적으로 정의한 Enum 클래스
 */

enum class Category(
    val categoryId: String,
    val categoryName: String,
    val imagePath: Int,
) {
    GAS(
        categoryId = "CG-3fa85f6425e811e", categoryName = "주유", imagePath = R.drawable.gas
    ),

    MART(
        categoryId = "CG-4fa85f6425ad1d3", categoryName = "대형마트", imagePath = R.drawable.mart
    ),

    TRANSPORTATION(
        categoryId = "CG-4fa85f6455cad4a",
        categoryName = "교통",
        imagePath = R.drawable.transportation
    ),

    EDUCATION(
        categoryId = "CG-6dd85f6425ez11o", categoryName = "교육/육아", imagePath = R.drawable.education
    ),

    COMMUNICATION(
        categoryId = "CG-7fa85f6425bc311", categoryName = "통신", imagePath = R.drawable.communication
    ),

    OVERSEAS(
        categoryId = "CG-8fa85f6425e1123", categoryName = "해외", imagePath = R.drawable.overseas
    ),

    DAILY_LIFE(
        categoryId = "CG-9ca85f66311a23d",
        categoryName = "생활",
        imagePath = R.drawable.icon_lifestyle
    ),

    FOOD(
        categoryId = "CG-5fa85f6425e812f", categoryName = "식비", imagePath = R.drawable.meal
    ),

    SHOPPING(
        categoryId = "CG-2fa85f6425e813g", categoryName = "쇼핑", imagePath = R.drawable.shopping
    ),

    CULTURE(
        categoryId = "CG-1fa85f6425e814h", categoryName = "문화/여가", imagePath = R.drawable.hobby
    );

    companion object {
        /**
         * 카테고리 ID로 카테고리 찾기
         */
        fun findById(categoryId: String): Category? {
            return values().find { it.categoryId == categoryId }
        }

        /**
         * 카테고리 이름으로 카테고리 찾기
         */
        fun findByName(categoryName: String): Category? {
            return entries.find { it.categoryName == categoryName }
        }

        /**
         * 모든 카테고리 데이터를 CategoryData 객체 리스트로 반환
         */
        fun getAllCategories(): List<Category> {
            return entries
        }
    }
}

/**
 * JSON 직렬화/역직렬화에 사용할 데이터 클래스
 */
data class CategoryData(
    val categoryId: String,
    val categoryName: String,
    val imagePath: String,
)