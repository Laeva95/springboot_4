package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.User;
import com.shinsunyoung.blog.dto.AddUserRequest;
import com.shinsunyoung.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 스프링 시큐리티의 회원가입 기능을 담당하는 서비스 클래스
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입에 성공한 유저의 Id를 반환
    public Long save(AddUserRequest dto){
        return userRepository
                // User 클래스의 builder 패턴으로 새 유저 생성해서 DB에 저장 후 id 반환
                .save(User
                        .builder()
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .build())
                .getId();

    }
}
