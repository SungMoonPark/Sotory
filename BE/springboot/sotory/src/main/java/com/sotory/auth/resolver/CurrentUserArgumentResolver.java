package com.sotory.auth.resolver;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.entity.AuthUser;
import com.sotory.auth.repository.AuthUserRepository;
import com.sotory.user.entity.User;
import com.sotory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserInfo.class)
                && parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            log.debug("인증 정보 없음 - authentication: {}", authentication);
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        try {
            UUID authUserId = UUID.fromString(authentication.getPrincipal().toString());
            log.debug("인증된 authUserId: {}", authUserId);

            AuthUser authUser = authUserRepository.findById(authUserId)
                    .orElseThrow(() -> {
                        log.warn("해당 authUserId로 사용자를 찾을 수 없음: {}", authUserId);
                        return new IllegalArgumentException("인증된 사용자를 찾을 수 없습니다.");
                    });

            User user = authUser.getUser();
            log.debug("유저 정보 조회 완료 - userId: {}, nickname: {}, userKey: {}",
                    user.getUserId(), user.getNickname(), user.getUserKey());

            return new CurrentUser(
                    user.getUserId(),
                    user.getNickname(),
                    user.getUserKey()
            );
        } catch (Exception e) {
            log.error("CurrentUserArgumentResolver 오류 발생: {}", e.getMessage(), e);
            return null;
        }

    }
}
