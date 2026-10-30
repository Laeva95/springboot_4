package com.shinsunyoung.blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console;

// Configuration: 스프링의 설정 클래스임을 표시
// EnableWebSecurity: 스프링 시큐리티의 웹 보안 설정을 활성화
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    // @Bean: 이 메소드가 반환하는 객체를 스프링 컨테이너에 등록
    // WebSecurityCustomizer: 특정 요청을 스프링 시큐리티 보안 필터 적용 대상에서 제외
    @Bean
    public WebSecurityCustomizer configure(){
        // web: WebSecurity 보안 설정 객체를 전달받아 보안에서 제외할 요청 경로 설정
        // ignoring(): 로그인 여부, 권한 체크 등 진행하지 않고 대상에서 제외
        // requestMatchers(): 보안 제외 대상 자원 경로 설정
        // toH2Console(): H2 DB 콘솔 주소를 자동으로 인식
        // "/static/**": static 폴더 내부의 모든 폴더, 파일들
        return web -> web.ignoring()
                                .requestMatchers(toH2Console())
                                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 스프링이 전달한 URL 권한 관리용 도구 auth를 받아서 Customize 메서드의 내부 동작을 정의
                .authorizeHttpRequests(auth -> auth
                        // 요청 경로 설정
                        .requestMatchers(
                                "/login",  // 로그인 페이지 경로
                                "/signup",          // 회원 가입 페이지  경로
                                "/user"             // 회원 가입 처리 API 경로
                        )

                        // permitAll(): 지정한 URL 요청은 인증 없이 허용
                        .permitAll()

                        // anyRequest(): 나머지 모든 요청 경로
                        .anyRequest()

                        // authenticated(): 반드시 로그인해야 접근 가능
                        .authenticated())

                // formLogin(): form 기반 로그인 설정
                .formLogin(formLogin -> formLogin

                        // loginPage(): 비 로그인 상태로 권한이 필요한 페이지 이동시 요청할 경로
                        .loginPage("/login")

                        // defaultSuccessUrl(): 로그인 성공 시 재요청 경로
                        .defaultSuccessUrl("/articles")
                )

                // logout(): 로그아웃 설정
                .logout(logout -> logout

                        // logoutSuccessUrl(): 로그아웃에 성공했을 때 요청 경로
                        .logoutSuccessUrl("/login")

                        // invalidateHttpSession(): 톰캣에서 HttpSession 객체 삭제
                        .invalidateHttpSession(true)
                )

                // csrf(): 사이트 간 위조 공격 방어 기능
                // AbstractHttpConfigurer::disable: 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 설정 완료한 SecurityFilterChain 객체를 생성하여 반환
                .build();
    }

    // 패스워드 인코더로 사용할 빈 등록
    // 비밀번호 암호화에 사용하는 객체
    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCryptPasswordEncoder
        // 단방향 해시 함수를 사용하여 암호화
        // salt 값을 자동 추가
        // 검증시 입력값을 해시화 하여 동일한지 비교
        return new BCryptPasswordEncoder();
    }


}
