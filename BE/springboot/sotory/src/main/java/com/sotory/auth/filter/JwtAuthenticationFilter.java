package com.sotory.auth.filter;

import com.sotory.auth.util.JwtProvider;
import com.sotory.common.exception.CustomException;
import com.sotory.user.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthenticationFilter 시작됨");

      //  String uri = request.getRequestURI();

        String token = resolveToken(request);
        log.debug("추출된 토큰: {}", token);
        if (!StringUtils.hasText(token)) {
            log.debug("토큰이 없습니다.");
        } else if (!jwtProvider.validateToken(token)) {
            log.debug("유효하지 않은 토큰입니다.");
        }

        if(StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            UUID userId = jwtProvider.extractUserId(token);
            UUID authUserId = jwtProvider.extractAuthUserId(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(authUserId, null, null);

            authentication.setDetails(
                    new WebAuthenticationDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}
