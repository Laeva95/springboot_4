package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.config.oauth.OAuth2SuccessHandler;
import com.shinsunyoung.blog.dto.CreateAccessTokenRequest;
import com.shinsunyoung.blog.dto.CreateAccessTokenResponse;
import com.shinsunyoung.blog.service.RefreshTokenService;
import com.shinsunyoung.blog.service.TokenService;
import com.shinsunyoung.blog.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;     // 토큰 발급 로직을 맡은 서비스
    private final RefreshTokenService refreshTokenService;
    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/api/token")
    public ResponseEntity<Void> createNewAccessToken(
            @CookieValue(
                    value = OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        try {
            // ① Refresh Token 검증 후 새 Access Token 발급
            String newAccessToken =
                    tokenService.createNewAccessToken(refreshToken);

            // ② 새로운 Access Token을 HttpOnly 쿠키에 저장
            CookieUtil.addHttpOnlyCookie(
                    response,
                    OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME,
                    newAccessToken,
                    TokenService.ACCESS_TOKEN_DURATION,
                    secureCookie
            );

            response.setHeader("Cache-Control", "no-store");

            // ③ 토큰 문자열을 응답 본문에 넣지 않음
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    // 로그아웃 시 db에 저장된 refresh token을 삭제하는 메소드
    @DeleteMapping("/api/refresh-token")
    public ResponseEntity<Void> deleteRefreshToken(
            @CookieValue(
                    value = OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // ① DB에 저장된 Refresh Token 삭제
        refreshTokenService.deleteByRefreshToken(refreshToken);

        // ② 브라우저의 Refresh Token 쿠키 만료
        CookieUtil.deleteCookie(
                request,
                response,
                OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME
        );

        // DB의 Refresh Token 삭제
        refreshTokenService.deleteByRefreshToken(refreshToken);

        // Refresh Token 쿠키 삭제
        CookieUtil.deleteHttpOnlyCookie(
                response,
                OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME,
                secureCookie
        );

        // Access Token 쿠키 삭제
        CookieUtil.deleteHttpOnlyCookie(
                response,
                OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME,
                secureCookie
        );
        // ③ 로그아웃 성공
        return ResponseEntity.ok().build();
    }
}