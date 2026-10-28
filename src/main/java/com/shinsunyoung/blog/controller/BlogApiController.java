package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.dto.ArticleResponse;
import com.shinsunyoung.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
    // Service 객체 주입
    private final BlogService blogService;

    // Post 요청을 처리하는 메소드
    // 게시글 하나 추가
    @PostMapping("/api/articles")
    // @RequestBody: 요청 본문에 담긴 데이터를 Java 객체로 변환해서 전달 받음
    // @RequestParam: name 속성의 값, 파라미터를 전달 받음
    // @PathVariable: URL 경로의 값을 가져옴
    // ResponseEntity<>: HTTP 응답 전체를 구성할 수 있도록 제공하는 클래스
    //                  HTTP 상태 코드, 응답 본문, 응답 헤더를 함께 설정 할 수 있음
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        // 매개변수 객체를 데이터베이스에 저장하고, 해당 엔티티 객체를 변수에 저장
        Article savedArticle = blogService.save(request);

        // 저장된 게시글 정보와 저장 성공 코드를 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    // Get 요청 처리
    // 게시글 전체 조회
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        // Service에서 반환받은 List<Article>을 List<ArticleResponse>로 변환
        List<ArticleResponse> articles = blogService.findAll()
                                                    .stream()
                                                    .map(ArticleResponse::new)
                                                    .toList();

        // 조회한 게시글 목록 dto 객체 배열을 반환
        return ResponseEntity.ok().body(articles);
    }

    // Get 요청 처리
    // 게시글 하나 조회
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        // 데이터베이스에서 id로 게시글 조회하여 ArticleResponse로 변환
        ArticleResponse article = new ArticleResponse(blogService.findById(id));

        // 조회한 게시글 dto 객체를 반환
        return ResponseEntity.ok().body(article);
    }

    // Delete 요청 처리
    // 게시글 하나 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        // 데이터베이스에서 id를 통해 게시글 삭제
        blogService.delete(id);

        // 응답 상태 코드 반환
        return ResponseEntity.ok().build();
    }
}
