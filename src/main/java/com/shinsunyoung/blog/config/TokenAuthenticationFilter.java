package com.shinsunyoung.blog.config;

import com.shinsunyoung.blog.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    private final TokenProvider tokenProvider;
    // 헤더 이름
    private final static String HEADER_AUTHORIZATION = "Authorization";
    // 토큰 앞에 붙는 접두사
    private final static String TOKEN_PREFIX = "Bearer ";

    // 요청이 들어오면 해당 필터 메소드가 실행됨
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // ① 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // ② 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        // ③ 가져온 토큰이 유효한지 확인하고, 유효할 때는 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            // 유효 기간이 남았으면 이 토큰으로 인증 정보 생성
            Authentication authentication = tokenProvider.getAuthentication(token);

            // 만든 사용자 인증 정보 객체를 스프링 시큐리티의 저장소에 넣음
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // ④ 다음 필터(또는 컨트롤러)로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    // Bearer 접두사를 제외한 토큰 값을 돌려줌
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;                           // 헤더가 없거나 Bearer로 시작하지 않으면 null
    }
}