package com.ssafy.sotory.data.dto.userinfo

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoUpdateNicknameRequest(
    val nickname: String,
)
