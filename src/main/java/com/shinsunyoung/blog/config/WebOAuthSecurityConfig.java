package com.shinsunyoung.blog.config;

import lombok.RequiredArgsConstructor;
import com.shinsunyoung.blog.config.jwt.TokenProvider;
import com.shinsunyoung.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shinsunyoung.blog.config.oauth.OAuth2SuccessHandler;
import com.shinsunyoung.blog.repository.RefreshTokenRepository;
import com.shinsunyoung.blog.service.OAuth2UserCustomService;
import com.shinsunyoung.blog.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    // ❶ 시큐리티 검사를 건너뛸 정적 파일 경로
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    // ❷ 실제 보안 규칙
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)          // 토큰 방식이므로 CSRF 끔
                .httpBasic(AbstractHttpConfigurer::disable)     // 기본 인증 창 끔
                .formLogin(AbstractHttpConfigurer::disable)     // 폼 로그인 끔 (OAuth2 로그인으로 대체)
                .logout(AbstractHttpConfigurer::disable)        // 기본 로그아웃 끔 (화면 자바스크립트에서 처리)
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 만들지 않음
                // ❸ 토큰 필터를 UsernamePasswordAuthenticationFilter 앞에 끼워 넣음 (10장 필터)
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token").permitAll()   // 토큰 재발급은 누구나
                        .requestMatchers("/api/**").authenticated()  // 나머지 API는 로그인 필요
                        .anyRequest().permitAll())                   // 화면 주소는 누구나
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")                         // 로그인 화면 주소
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserCustomService))  // 사용자 정보 처리 담당
                        .successHandler(oAuth2SuccessHandler()))        // 로그인 성공 후 처리 담당
                .exceptionHandling(ex -> ex
                        // /api/ 로 시작하는 요청이 인증 없이 들어오면 401 응답 (화면 이동 대신)
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                PathPatternRequestMatcher.pathPattern("/api/**")))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))
                .build();
    }

    // 로그인 성공 핸들러 빈 등록
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    // 10장의 토큰 필터 빈 등록
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}