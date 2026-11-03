package com.shinsunyoung.blog.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.shinsunyoung.blog.config.jwt.TokenProvider;
import com.shinsunyoung.blog.domain.RefreshToken;
import com.shinsunyoung.blog.domain.User;
import com.shinsunyoung.blog.repository.RefreshTokenRepository;
import com.shinsunyoung.blog.service.UserService;
import com.shinsunyoung.blog.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User =
                (OAuth2User) authentication.getPrincipal();

        User user = userService.findByEmail(
                (String) oAuth2User.getAttributes().get("email")
        );

        // ① Refresh Token 생성
        String refreshToken =
                tokenProvider.generateToken(
                        user, REFRESH_TOKEN_DURATION
                );

        // ② Refresh Token DB 저장
        saveRefreshToken(user.getId(), refreshToken);

        // ③ Refresh Token을 HttpOnly 쿠키에 저장
        CookieUtil.addHttpOnlyCookie(
                response,
                REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                REFRESH_TOKEN_DURATION,
                secureCookie
        );

        // ④ Access Token 생성
        String accessToken =
                tokenProvider.generateToken(
                        user, ACCESS_TOKEN_DURATION
                );

        // ⑤ Access Token도 HttpOnly 쿠키에 저장
        CookieUtil.addHttpOnlyCookie(
                response,
                ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                ACCESS_TOKEN_DURATION,
                secureCookie
        );

        // ⑥ 민감한 응답의 캐싱 방지
        response.setHeader("Cache-Control", "no-store");

        // ⑦ OAuth2 임시 정보 삭제
        super.clearAuthenticationAttributes(request);

        authorizationRequestRepository
                .removeAuthorizationRequestCookies(request, response);

        // ⑧ 토큰 없이 글 목록으로 이동
        getRedirectStrategy().sendRedirect(
                request,
                response,
                REDIRECT_PATH
        );
    }

    // 리프레시 토큰을 사용자 ID와 함께 저장 (이미 있으면 값만 갱신)
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));
        refreshTokenRepository.save(refreshToken);
    }

    // 리프레시 토큰을 쿠키에 담는다 (14일)
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response,
                                         String token) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, token, cookieMaxAge);
    }

    // 액세스 토큰을 URL의 token 파라미터로 붙인 주소를 만든다
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}