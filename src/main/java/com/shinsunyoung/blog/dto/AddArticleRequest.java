package com.shinsunyoung.blog.dto;

import com.shinsunyoung.blog.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    // title과 content를 통해 Article 엔티티 생성
    public Article toEntity(){
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
