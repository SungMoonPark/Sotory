package com.sotory.auth.dto.request;

import com.sotory.auth.dto.BaseTokenDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequestDTO extends BaseTokenDTO {
    public LogoutRequestDTO(String refreshToken) {
        super(refreshToken);
    }
}