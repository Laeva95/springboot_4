package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
    // Service 객체 주입
    private final BlogService blogService;

    // Post 요청을 처리하는 메소드
    @PostMapping("/api/articles")
    // @RequestBody: 요청 본문에 담긴 데이터를 Java 객체로 변환해서 전달 받음
    // @RequestParam: name 속성의 값, 파라미터를 전달 받음
    // @PathVariable: URL 경로의 값을 가져옴
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        // 매개변수 객체를 데이터베이스에 저장하고, 해당 엔티티 객체를 변수에 저장
        Article savedArticle = blogService.save(request);

        // 저장된 게시글 정보와 저장 성공 코드를 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

}
