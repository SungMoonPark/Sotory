package com.sotory.auth.dto.response;

import com.sotory.auth.dto.BaseTokenDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO extends BaseTokenDTO {
    private String accessToken;
  //  private String deviceId;
  //  private String appVersion;

    @Builder
    public AuthResponseDTO(String accessToken, String refreshToken) {
        super(refreshToken);
        this.accessToken = accessToken;
    }
}
