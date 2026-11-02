package com.shinsunyoung.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// CreateAccessTokenResponse.java (dto 패키지)
@AllArgsConstructor                      // 모든 필드를 받는 생성자 자동 생성
@Getter
public class CreateAccessTokenResponse {
    private String accessToken;           // 응답으로 돌려줄 새 액세스 토큰
}