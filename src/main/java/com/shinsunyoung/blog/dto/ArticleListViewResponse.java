package com.shinsunyoung.blog.dto;

import com.shinsunyoung.blog.domain.Article;
import lombok.Getter;

@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    // 생성자
    // Article 엔티티를 매개변수로 전달받아 변수에 입력
    public ArticleListViewResponse(Article article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
