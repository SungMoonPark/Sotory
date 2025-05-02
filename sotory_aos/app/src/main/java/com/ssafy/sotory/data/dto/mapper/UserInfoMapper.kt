package com.ssafy.sotory.data.dto.mapper

import com.ssafy.sotory.data.dto.userinfo.UserInfoResponse
import com.ssafy.sotory.domain.userinfo.UserInfoModel

fun UserInfoResponse.toDomain(): UserInfoModel {
    return UserInfoModel(
        userId = userId,
        nickname = nickname,
        birthday = birthday,
        gender = gender
    )
}