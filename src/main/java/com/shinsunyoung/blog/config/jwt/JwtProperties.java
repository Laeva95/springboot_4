package com.shinsunyoung.blog.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
// @ConfigurationProperties(): application.yaml 파일 내의 "jwt" 항목 값을 변수에 매핑
@ConfigurationProperties("jwt")
public class JwtProperties {
    // 토큰 발급자
    private String issuer;

    // 토큰 비밀 키
    private String secretKey;
}
