package com.shinsunyoung.blog.dto;

import com.shinsunyoung.blog.domain.Article;
import lombok.Getter;

@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    // 엔티티를 매개변수로 전달 받아서 생성자 호출
    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
