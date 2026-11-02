package com.shinsunyoung.blog.config.jwt;

import com.shinsunyoung.blog.domain.User;
import com.shinsunyoung.blog.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TokenProviderTest {
    // 테스트에 사용할 스프링 빈 자동 주입
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    // ① generateToken() 검증 테스트
    // JWT 토큰을 제대로 만드는지 확인
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given: 테스트용 유저를 DB에 저장
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when: 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then: 토큰 안의 id 값이 테스트 유저의 id와 같은지 확인
        // Jwts.parser(): 토큰 정보를 읽는 메소드
        // setSigningKey(): 서명에 사용하는 비밀키 설정
        // parseClaimsJws(): 토큰을 실제로 읽는 메소드. 서명, 기한 확인
        // Long id를 꺼냄
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        // 생성한 토큰의 id와 db에 저장한 유저 id가 동일한지 확인
        assertThat(userId).isEqualTo(testUser.getId());
    }

    // ② validToken() 검증 테스트 (만료된 토큰)
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given: 만료 시각을 7일 전으로 설정한 토큰 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when: 유효성 검증
        // 유효 기간이 지났으므로 false 반환
        boolean result = tokenProvider.validToken(token);

        // then: 유효하지 않으니 false
        assertThat(result).isFalse();
    }

    // ② validToken() 검증 테스트 (유효한 토큰)
    @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given: 기본값(14일 뒤 만료)으로 만든 토큰
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when: 유효성 검증
        // 유효 기간이 남았으므로 true 반환
        boolean result = tokenProvider.validToken(token);

        // then: 유효하니 true
        assertThat(result).isTrue();
    }

    // ③ getAuthentication() 검증 테스트
    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given: subject가 user@email.com인 토큰 생성
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when: 생성한 토큰의 인증 객체 가져오기
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then: 인증 객체의 사용자 이름이 토큰의 subject와 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername())
                .isEqualTo(userEmail);
    }

    // ④ getUserId() 검증 테스트
    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given: id 클레임에 1을 넣은 토큰
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when: 토큰에서 유저 ID 가져오기
        Long userIdByToken = tokenProvider.getUserId(token);

        // then: 가져온 값이 1과 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}