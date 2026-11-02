package com.shinsunyoung.blog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken;          // 클라이언트가 보낼 리프레시 토큰
}

