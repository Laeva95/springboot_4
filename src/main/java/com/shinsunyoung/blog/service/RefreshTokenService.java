package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.RefreshToken;
import com.shinsunyoung.blog.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;   // DB 조회 담당

    // 토큰 문자열로 리프레시 토큰 객체를 찾아 돌려줌
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}