package com.shinsunyoung.blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 게시글 하나의 정보를 저장하는 엔티티 객체 클래스
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    // Id, 제목, 내용을 저장

    // id: 기본키 제약조건, 자동으로 숫자가 1씩 증가하여 저장됨
    // 값 수정 불가능, null 값 저장 불가능
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // title: null 값 저장 불가능
    @Column(name = "title", nullable = false)
    private String title;

    // content: null 값 저장 불가능
    @Column(name = "content", nullable = false)
    private String content;

    // 빌더 패턴으로 객체 생성
    @Builder
    public Article(String title, String content){
        this.title = title;
        this.content = content;
    }

    // 엔티티 생성 시간 저장
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 엔티티 수정 시간 저장
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 게시글 수정 요청을 받았을 때 제목, 내용을 수정하는 메소드
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
