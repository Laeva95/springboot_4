package com.shinsunyoung.blog.service;

import lombok.RequiredArgsConstructor;
import com.shinsunyoung.blog.domain.User;
import com.shinsunyoung.blog.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

// 구글 사용자 정보를 우리 사이트의 회원으로 저장하는 클래스
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 구글이 사용자 정보를 돌려주면 호출된다
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);   // 구글 API에서 사용자 정보 조회
        saveOrUpdate(user);                              // 우리 DB에 저장 또는 갱신
        return user;
    }

    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))   // 이미 있으면 이름만 갱신
                .orElse(User.builder()                   // 없으면 새로 만든다
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);
    }
}