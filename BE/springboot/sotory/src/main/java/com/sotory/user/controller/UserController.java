package com.sotory.user.controller;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.common.ApiResponse;
import com.sotory.user.dto.requset.UserNicknameRequest;
import com.sotory.user.dto.response.NicknameResponse;
import com.sotory.user.dto.response.UserInfoResponse;
import com.sotory.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "사용자 정보 조회", tags = "users")
    public ResponseEntity<?> getUserInfo(@CurrentUserInfo CurrentUser user) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(user.getUserId());
        log.info("정보조회 성공 - 유저 이름 : {}", user.getNickname());
        return ResponseEntity.ok(ApiResponse.success(userInfoResponse));
    }

    @PatchMapping
    @Operation(summary = "닉네임 변경", tags = "users")
    public ResponseEntity<?> addUserInfo(
            @CurrentUserInfo CurrentUser user,
            @RequestBody UserNicknameRequest userNicknameRequest
            ) {
        log.info("닉네임을 변경합니다");
        NicknameResponse nicknameResponse = null;
        try {
            nicknameResponse = userService.updateUserNickname(user.getUserId(), userNicknameRequest);
        } catch (Exception e) {
            log.error("닉네임 변경 중 오류 발생: ", e);
            throw e;
        }
        log.info("닉네임 변경 완료 - 변경된 닉네임 : {}", user.getNickname());
        return ResponseEntity.ok(ApiResponse.success(nicknameResponse));
    }
}
