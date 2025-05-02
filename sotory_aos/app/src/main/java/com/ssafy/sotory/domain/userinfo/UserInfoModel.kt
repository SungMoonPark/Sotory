package com.ssafy.sotory.domain.userinfo

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoModel(
    val userId: String,
    val nickname: String,
    val birthday: String?,
    val gender: Gender?
)

@Serializable
enum class Gender {
    M, F
}