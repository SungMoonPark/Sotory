package com.sotory.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenUpdateDTO extends BaseTokenDTO {
    private LocalDateTime expiresAt;
    private LocalDateTime issuedAt;

    @Builder
    public TokenUpdateDTO(String refreshToken, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        super(refreshToken);
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}
