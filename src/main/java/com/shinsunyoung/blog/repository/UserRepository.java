package com.shinsunyoung.blog.repository;

import com.shinsunyoung.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// users 테이블에 접근하기 위한 Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 쿼리 메소드
    // 엔티티의 변수 명을 통해 메소드를 실행
    // 특정 조건을 포함시킬 수 있음
    // email을 통해 사용자 정보 조회
    Optional<User> findByEmail(String email);
}
