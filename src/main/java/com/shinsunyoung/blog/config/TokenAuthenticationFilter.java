package com.shinsunyoung.blog.config;

import com.shinsunyoung.blog.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// TokenAuthenticationFilter.java (config 패키지)
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {  // 요청당 한 번 실행되는 필터
    private static final String ACCESS_TOKEN_COOKIE_NAME =
            "access_token";
    private final TokenProvider tokenProvider;
    // 헤더 이름
    private final static String HEADER_AUTHORIZATION = "Authorization";
    // 토큰 앞에 붙는 접두사
    private final static String TOKEN_PREFIX = "Bearer ";

    // 요청이 들어오면 해당 필터 메소드가 실행됨
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ① 쿠키에서 Access Token 가져오기
        String token = getAccessTokenFromCookie(request);

        // 기존 방식과의 호환성을 위해 Authorization 헤더도 허용
        if (token == null) {
            String authorizationHeader =
                    request.getHeader(HEADER_AUTHORIZATION);

            token = getAccessToken(authorizationHeader);
        }

        // ② 토큰 유효성 검사
        if (tokenProvider.validToken(token)) {

            // ③ 인증 객체 생성
            Authentication authentication =
                    tokenProvider.getAuthentication(token);

            // ④ SecurityContextHolder에 등록
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        // ⑤ 다음 필터로 전달
        filterChain.doFilter(request, response);
    }

    // Bearer 접두사를 제외한 토큰 값을 돌려줌
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;                           // 헤더가 없거나 Bearer로 시작하지 않으면 null
    }

    private String getAccessTokenFromCookie(
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (ACCESS_TOKEN_COOKIE_NAME.equals(
                        cookie.getName())) {

                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}