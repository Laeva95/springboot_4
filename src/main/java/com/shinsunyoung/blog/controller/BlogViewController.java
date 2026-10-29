package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.ArticleListViewResponse;
import com.shinsunyoung.blog.dto.ArticleViewResponse;
import com.shinsunyoung.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {
    private final BlogService blogService;

    // http://localhost:8080/articles
    @GetMapping("/articles")
    public String getArticles(Model model){
        // 서비스에서 조회한 Article 엔티티들을 dto 객체들로 변환
        List<ArticleListViewResponse> articles = blogService.findAll()
                                                            .stream()
                                                            .map(ArticleListViewResponse::new)
                                                            .toList();

        // 게시글 dto 들을 model 객체에 저장
        model.addAttribute("articles", articles);

        // articleList.html 뷰 페이지로 이동
        return "articleList";
    }

    // http://localhost:8080/articles/1
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        // id를 통해 게시글 검색
        Article article = blogService.findById(id);

        // 모델 객체에 저장
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    // 새 글 작성, 글 수정 화면 요청
    // http://localhost:8080/new-article
    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model){
        if (id == null){
            model.addAttribute("article", new ArticleViewResponse());
        } else{
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }
}
