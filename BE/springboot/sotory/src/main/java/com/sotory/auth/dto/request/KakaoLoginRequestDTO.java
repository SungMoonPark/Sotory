package com.sotory.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoLoginRequestDTO {
    private String accessToken;
    // private String deviceId;
    private String deviceType;
    private String appVersion;

}
