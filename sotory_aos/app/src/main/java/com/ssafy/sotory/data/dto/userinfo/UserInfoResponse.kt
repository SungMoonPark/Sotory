package com.ssafy.sotory.data.dto.userinfo

import com.ssafy.sotory.domain.userinfo.Gender
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val userId: String,
    val nickname: String,
    val birthday: String?,
    val gender: Gender?
)