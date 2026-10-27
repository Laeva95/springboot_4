package com.shinsunyoung.blog.repository;

import com.shinsunyoung.blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// article 테이블에 접근하는 기능 목록
public interface BlogRepository extends JpaRepository<Article, Long> {
}
