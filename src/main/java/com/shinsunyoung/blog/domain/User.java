package com.shinsunyoung.blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// 테이블 이름을 user가 아닌 users로 생성하여 매핑
// sql 예약어인 user와의 충돌 방지를 위해 users로 생성
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {
    // 테이블 열
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // updatable = false: 데이터 수정 불가능
    @Column(name = "id", updatable = false)
    private Long id;

    // nullable = false: null 값 불가능
    // unique = true: 중복 값 불가능
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // nullable = true: 기본적으로 true. OAuth2 소셜 로그인의 경우 비밀번호가 없음
    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    // 회원가입 시 사용하기 위한 빌더
    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
//        this.nickname = nickname;
    }

    // 기타 UserDetails 필수 오버라이드 메서드 (true 반환)
    // 시큐리티 엔진이 인가 및 인증을 진행할 때 자동으로 호출되는 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 회원에게 기본적으로 "user" 권한 부여
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 시큐리티에서 식별자로 사용할 사용자 계정 이메일을 반환
    @Override
    public String getUsername() { return email; }

    // 시큐리티가 암호화된 비밀번호를 비교할 수 있도록 반환
    @Override
    public String getPassword() { return password; }

    // 계정 만료 여부. 계정이 탈퇴했는지 확인
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부. 비밀번호 5회 실패 등으로 계정이 잠겼는지 확인
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 변경 주기가 지났는지 확인
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 되어있는지 확인
    @Override
    public boolean isEnabled() {
        return true;
    }
}