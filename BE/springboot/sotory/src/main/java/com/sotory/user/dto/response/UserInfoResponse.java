package com.sotory.user.dto.response;

import com.sotory.user.entity.User;

import java.util.UUID;

public record UserInfoResponse(
    UUID userId,
    String nickname,
    String birthday,
    String gender
) {
    public UserInfoResponse(User user){
        this(user.getUserId(), user.getNickname(), user.getBirthday(), String.valueOf(user.getGender()));
    }
}
