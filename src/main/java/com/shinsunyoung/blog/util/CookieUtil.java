package com.shinsunyoung.blog.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

// 쿠키 관리 클래스
public class CookieUtil {

    // 요청값(이름, 값, 만료 기간)을 바탕으로 쿠키를 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");          // 사이트 모든 경로에서 쿠키 사용
        cookie.setMaxAge(maxAge);     // 만료 시간(초 단위)
        response.addCookie(cookie);
    }

    // 쿠키 이름을 받아 같은 이름의 쿠키를 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");   // 값을 비우고
                cookie.setPath("/");
                cookie.setMaxAge(0);   // 만료 시간을 0으로 하면 브라우저가 바로 지운다
                response.addCookie(cookie);
            }
        }
    }

    // 객체를 직렬화한 뒤 Base64로 바꿔 쿠키 값으로 쓸 수 있게 만든다
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키 값을 Base64로 풀고 역직렬화해 원래 객체로 되돌린다
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}