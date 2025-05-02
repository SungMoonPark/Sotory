package com.sotory.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CurrentUser {
    private UUID userId;
    private String nickname;
    private String userKey;
}
