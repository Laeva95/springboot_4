package com.shinsunyoung.blog.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtFactory {

    private String subject = "test@email.com";                            // 기본 제목(sub)
    private Date issuedAt = new Date();                                   // 기본 발급 시각
    private Date expiration = new Date(new Date().getTime()
            + Duration.ofDays(14).toMillis());                            // 기본 만료 시각(14일 뒤)

    private Map<String, Object> claims = emptyMap();                  // 추가 클레임(기본은 비어 있음)

    // 빌더 패턴을 사용해 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration,
                      Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;          // 값이 있으면 쓰고 없으면 기본값
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    // 기본값으로만 만든 객체를 반환
    public static JwtFactory withDefaultValues() {
        return JwtFactory.builder().build();
    }

    // jjwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }
}
