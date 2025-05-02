package com.sotory.auth.repository;

import com.sotory.auth.entity.AuthToken;
import com.sotory.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    Optional<AuthToken> findByAuthUser(AuthUser user);
    Optional<AuthToken> findByRefreshToken(String refreshToken);
}
