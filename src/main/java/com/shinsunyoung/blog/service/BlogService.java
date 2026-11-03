package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.dto.UpdateArticleRequest;
import com.shinsunyoung.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    // Repository 객체 주입
    private final BlogRepository blogRepository;

    // 게시글 하나 추가 메소드
    public Article save(AddArticleRequest request, String userName){
        // 매개변수 객체를 엔티티 객체로 변경하여 저장 후 반환
        return blogRepository.save(request.toEntity(userName));
    }

    // 게시글 목록 전체 조회 메소드
    public List<Article> findAll(){
        return blogRepository.findAll();
    }

    // 게시글 하나 조회 메소드
    public Article findById(long id){
        // id로 게시글 하나 조회
        // 실패 시 예외 처리
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Transactional
    // 게시글 하나 삭제 메소드
    public void delete(long id){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }


    private static void authorizeArticleAuthor(Article article) {

        String userName = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        System.out.println("게시글 작성자: " + article.getAuthor());
        System.out.println("로그인 사용자: " + userName);

        if (!article.getAuthor().equals(userName)) {
            throw new AccessDeniedException(
                    "본인이 작성한 게시글만 수정하거나 삭제할 수 있습니다."
            );
        }
    }
}
