package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.dto.ArticleListViewResponse;
import com.shinsunyoung.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {
    private final BlogService blogService;

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
}
