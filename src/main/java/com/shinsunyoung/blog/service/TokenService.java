package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.config.jwt.TokenProvider;
import com.shinsunyoung.blog.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;               // 토큰 생성·검증 담당
    private final RefreshTokenService refreshTokenService;   // 리프레시 토큰 조회 담당
    private final UserService userService;                   // 사용자 조회 담당

    public String createNewAccessToken(String refreshToken) {
        // ① 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        // ② 리프레시 토큰으로 사용자 ID 찾기
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        // ③ 사용자 ID로 사용자 찾기
        User user = userService.findById(userId);

        // ④ 새 액세스 토큰 생성(유효 기간 2시간)
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}