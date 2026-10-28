package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    // 게시글 하나 삭제 메소드
    public void delete(long id){
        // id를 통해 게시글이 존재하는지 확인
        // existsById(id): id 값을 통해 데이터베이스에 데이터가 존재하는지 확인
        if(!blogRepository.existsById(id)){
            // 404 에러 및 메세시 출력
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found: " + id);
        }

        // id로 게시글 하나 삭제
        blogRepository.deleteById(id);
    }
}
