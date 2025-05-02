package com.sotory.auth.service;

import com.sotory.auth.dto.request.FintechRegisterRequestDTO;
import com.sotory.auth.dto.response.AuthResponseDTO;
import com.sotory.auth.dto.response.FintechRegisterResponseDTO;
import com.sotory.auth.dto.response.KakaoUserInfoResponseDTO;
import com.sotory.auth.dto.TokenUpdateDTO;
import com.sotory.auth.entity.AuthToken;
import com.sotory.auth.entity.AuthUser;
import com.sotory.auth.repository.AuthTokenRepository;
import com.sotory.auth.repository.AuthUserRepository;
import com.sotory.auth.util.JwtProvider;
import com.sotory.common.exception.CustomException;
import com.sotory.common.exception.ErrorCode;
import com.sotory.core.config.JwtProperties;
import com.sotory.core.config.FintechProvider;
import com.sotory.user.entity.User;
import com.sotory.user.entity.Gender;
import com.sotory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final KakaoOAuthService kakaoOAuthService;
    private final AuthUserRepository authUserRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_PREFIX = "refresh:";
    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final FintechProvider fintechProvider;
    private final FintechService fintechService;

    private void saveRefreshToken(AuthUser user, String refreshToken, long version) {
        // Redis 키는 userId 기준으로 안전하게 구성
        String key = REFRESH_PREFIX + user.getUser().getUserId();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        if (factory instanceof LettuceConnectionFactory lettuceFactory) {
            log.info("Redis 서버 접속 정보 - host: {}, port: {}, db: {}",
                    lettuceFactory.getHostName(),
                    lettuceFactory.getPort(),
                    lettuceFactory.getDatabase());
        }

        String pingResponse = redisTemplate.getConnectionFactory().getConnection().ping();
        log.info("Redis ping response: {}", pingResponse);
        try {
            redisTemplate.opsForValue().set(key, refreshToken, jwtProperties.getRefreshExpiration(), TimeUnit.MILLISECONDS);
            log.info("Redis 저장 완료: key = {}, token = {}, ttl = {}ms", key, refreshToken, jwtProperties.getRefreshExpiration());
        } catch (Exception e) {
            log.error("Redis 저장 실패: {}", e.getMessage(), e);
        }

        String confirm = redisTemplate.opsForValue().get(key);
        log.info("Redis 저장 확인 - key: {}, 읽은 값: {}", key, confirm);

        // DTO 생성
        TokenUpdateDTO dto = TokenUpdateDTO.builder()
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshExpiration())))
                .build();

        // DB 저장 (존재하면 갱신, 없으면 생성)
        AuthToken token = authTokenRepository.findByAuthUser(user)
                .map(existing -> {
                    existing.updateWithDto(dto);
                    existing.updateVersion(version);
                    return existing;
                })
                .orElse(AuthToken.builder()
                        .authUser(user)
                        .refreshToken(dto.getRefreshToken())
                        .issuedAt(dto.getIssuedAt())
                        .expiresAt(dto.getExpiresAt())
                        .valid(true)
                        .version(version)
                        .build()
                );

        authTokenRepository.save(token);
    }

    public AuthResponseDTO reissueToken(String refreshToken) {
        // JWT에서 ID 추출
        UUID authUserId = jwtProvider.extractAuthUserId(refreshToken);
        UUID userId = jwtProvider.extractUserId(refreshToken);

        long version = jwtProvider.extractTokenVersion(refreshToken);

        String redisKey = REFRESH_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_REDIS);
        }

        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_TOKEN_NOT_FOUND));

        AuthToken authToken = authTokenRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_DB));

        if (!authToken.isValid() || !authToken.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_DB);
        }

        if (!authToken.getVersion().equals(version)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_VERSION_MISMATCH);
        }


        String newAccessToken = jwtProvider.generateAccessToken(authUserId, userId);

        long remainingMillis = Duration.between(LocalDateTime.now(), authToken.getExpiresAt()).toMillis();
        String newRefreshToken = refreshToken;

        if (remainingMillis <= jwtProperties.getRefreshRenewThreshold()) {
            long newVersion = version + 1;
            redisTemplate.delete(redisKey);
            newRefreshToken = jwtProvider.generateRefreshToken(authUserId, userId,version);
            try {
                saveRefreshToken(authUser, newRefreshToken, newVersion); // 예외 처리 추가
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("동시 토큰 재발급 충돌 발생: userId={}, version={}", userId, newVersion);
                throw new CustomException(ErrorCode.TOKEN_REISSUE_CONFLICT); // 필요시 재시도 유도
            }
        }

        return new AuthResponseDTO(newAccessToken, newRefreshToken);
    }


    private AuthUser registerUser(KakaoUserInfoResponseDTO kakaoUserInfo) {
        KakaoUserInfoResponseDTO.KakaoAccount account = kakaoUserInfo.getKakao_account();
        String nickname = account.getProfile().getNickname();
        String birthday = account.getBirthday(); // MMdd 형식
        String gender = account.getGender();     // male, female

        log.info("{} ", kakaoUserInfo.toString());

        // 기존 AuthUser 있나 확인
        Optional<AuthUser> existingAuthUser = authUserRepository.findByProviderAndProviderUserId("kakao", kakaoUserInfo.getId());
        if (existingAuthUser.isPresent()) {
            return existingAuthUser.get(); // 이미 가입된 사용자면 그대로 반환
        }

//       FintechRegisterResponseDTO userKey = fintechService.generedUserFintechKey(new FintechRegisterRequestDTO(fintechProvider.getKey(), "ssafy@ssafy.co"));
//        log.info("userKey 생성 성공:{}", userKey.userKey());
        // User 생성
        User user = User.builder()
                .nickname(nickname)
                .birthday(birthday) // 문자열 그대로 저장 (예: 0130)
                .gender(parseGender(gender)) // enum 변환
                .userKey(fintechProvider.getUserKey())
                .build();
        userRepository.save(user);
        log.info("user 생성 성공:{} {} ",user.getUserId(), user.getUserKey());
        // AuthUser 생성
        AuthUser authUser = AuthUser.builder()
                .provider("kakao")
                .providerUserId(String.valueOf(kakaoUserInfo.getId()))
                .user(user)
                .isDeleted(false)
                .build();
        return authUserRepository.save(authUser);
    }

    public AuthResponseDTO kakaoLogin(String kakaoAccessToken) {
        KakaoUserInfoResponseDTO kakaoInfo = kakaoOAuthService.getKakaoUserInfo(kakaoAccessToken);
        String kakaoId = kakaoInfo.getId();

        log.info("유저 정보:{} {} ",kakaoInfo.toString(), kakaoId);
        AuthUser user = authUserRepository
                .findByProviderAndProviderUserId("kakao", kakaoId)
                .orElseGet(() -> registerUser(kakaoInfo));

        UUID authUserId = user.getId();            // AuthUser PK
        UUID userId = user.getUser().getUserId();           // 진짜 유저 UUID

        AuthToken authToken = authTokenRepository.findByAuthUser(user).orElse(null);
        long version = (authToken != null) ? authToken.getVersion() + 1 : 1;

        log.info("Redis Key 생성용 userId: {}", user.getUser().getUserId());
        log.info("Redis Key 생성용 authUserId: {}", user.getId());
        String accessToken = jwtProvider.generateAccessToken(authUserId, userId);
        String refreshToken = jwtProvider.generateRefreshToken(authUserId, userId, version);

        saveRefreshToken(user, refreshToken, version);
        log.info("user:{} refreshToken:{}", userId, refreshToken);

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        UUID authUserId = jwtProvider.extractAuthUserId(accessToken);

        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        AuthToken authToken = authTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (!authToken.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String redisKey = REFRESH_PREFIX + authUser.getUser().getUserId();
        redisTemplate.delete(redisKey);

        authToken.invalidate();
        authTokenRepository.save(authToken);

        log.info("로그아웃 성공: 사용자 ID {}, refreshToken: {}", authUserId, refreshToken);
    }


    private Gender parseGender(String gender) {
        if (gender == null) return null;
        return gender.equalsIgnoreCase("male") ? Gender.M : Gender.F;
    }
}

