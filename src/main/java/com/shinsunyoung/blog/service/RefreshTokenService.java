package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.RefreshToken;
import com.shinsunyoung.blog.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;   // DB 조회 담당

    // 토큰 문자열로 리프레시 토큰 객체를 찾아 돌려줌
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    // Refresh Token 삭제
    @Transactional
    public void deleteByRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}