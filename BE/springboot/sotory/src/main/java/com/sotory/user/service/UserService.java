package com.sotory.user.service;

import com.sotory.common.exception.ErrorCode;
import com.sotory.user.dto.requset.UserNicknameRequest;
import com.sotory.user.dto.response.NicknameResponse;
import com.sotory.user.entity.User;
import com.sotory.user.dto.response.UserInfoResponse;
import com.sotory.user.exception.UserException;
import com.sotory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원가입

    // 사용자 정보 조회
    public UserInfoResponse getUserInfo(UUID userId) {
        log.info("유저 정보 조회 {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        return new UserInfoResponse(user);
    }


    public NicknameResponse updateUserNickname(UUID userId, UserNicknameRequest userNickname) {
        log.info("유저 닉네임 변경 {}", userNickname);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        log.info("유저 닉네임을 변경했습니다.");
        user.setNickname(userNickname.nickname());
        log.info("변경된 닉네임을 저장합니다.");
        userRepository.save(user);
        return new NicknameResponse(user.getNickname());
    }

}
