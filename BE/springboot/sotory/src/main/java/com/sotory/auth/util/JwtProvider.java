package com.sotory.auth.util;

import com.sotory.common.exception.CustomException;
import com.sotory.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    @Value("${jwt.access-expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidity;

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UUID authUserId, UUID userId) {
        return generateToken(authUserId, userId, accessTokenValidity);
    }

    public String generateRefreshToken(UUID authUserId, UUID userId, long version) {
        return generateToken(authUserId, userId, refreshTokenValidity, version);
    }


    private String generateToken(UUID authUserId, UUID userId, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);


        return Jwts.builder()
                // .setId(UUID.randomUUID().toString())
                .setSubject(authUserId.toString())
                .claim("userId", userId.toString())// 1. 주제 설정
                .setIssuedAt(now)                     // 2. 발급 시간 설정
                .setExpiration(expiry)                // 3. 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 4. 서명 생성
                .compact();                           // 5. JWT 토큰 생성
    }

    private String generateToken(UUID authUserId, UUID userId, long validity, long version) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);


        return Jwts.builder()
               // .setId(UUID.randomUUID().toString())
                .setSubject(authUserId.toString())
                .claim("userId", userId.toString())// 1. 주제 설정
                .claim("version", version)
                .setIssuedAt(now)                     // 2. 발급 시간 설정
                .setExpiration(expiry)                // 3. 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 4. 서명 생성
                .compact();                           // 5. JWT 토큰 생성
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (JwtException e) {
            log.warn("JWT 파싱 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 401: 유효하지 않은 토큰
        }
    }


    public UUID extractUserId(String token) {
        String uuidString = getClaims(token).get("userId", String.class);
        return UUID.fromString(uuidString);
    }

    public UUID extractAuthUserId(String token) {
        String subject = getClaims(token).getSubject(); // sub는 String
        return UUID.fromString(subject);
    }

    public Long extractTokenVersion(String token) {
        return getClaims(token).get("version", Long.class);
    }

}
