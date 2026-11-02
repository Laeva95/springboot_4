package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// UserDetailsService 인터페이스의 loadUserByUsername 메소드를 구현
public class UserDetailService implements UserDetailsService {
    // 생성자를 통해 의존성 주입(DI)
    private final UserRepository userRepository;

    // 스프링 시큐리티에서 로그인을 처리하는 메소드
    // 로그인 요청시 해당 메소드가 호출되어 로그인 처리
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                // UserNameNotFoundException(): 유저 이름을 찾을 수 없음
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다. email = " + email));
    }
}
