package com.sotory.auth.entity;

import com.sotory.auth.dto.TokenUpdateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="auth_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향 연관관계로 변경
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    private String refreshToken;
    private String deviceId;
    private String deviceName;
    private String appVersion;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private boolean valid;

    @Version
    private Long version;

    public void updateWithDto(TokenUpdateDTO dto) {
        this.refreshToken = dto.getRefreshToken();
        this.issuedAt = dto.getIssuedAt();
        this.expiresAt = dto.getExpiresAt();
        this.valid = true;
    }

    public void updateVersion(long newVersion) {
        if (newVersion <= this.version) {
            throw new IllegalArgumentException("버전은 증가해야 합니다.");
        }
        this.version = newVersion;
    }

    public void invalidate() {
        this.valid = false;
    }
}
