package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlogService {
    // Repository 객체 주입
    private final BlogRepository blogRepository;

    // 게시글 하나 추가 메소드
    public Article save(AddArticleRequest request){
        // 매개변수 객체를 엔티티 객체로 변경하여 저장 후 반환
        return blogRepository.save(request.toEntity());
    }
}
