package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.domain.Article;
import com.shinsunyoung.blog.dto.AddArticleRequest;
import com.shinsunyoung.blog.dto.UpdateArticleRequest;
import com.shinsunyoung.blog.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 테스트를 시작 할 때 Blog 웹 어플리케이션 전체 설정으로 스프링 컨테이너를 생성함
@SpringBootTest

// MockMvc 객체를 만들어서 스프링 컨테이너에 등록
@AutoConfigureMockMvc
class BlogApiControllerTest {

    // MockMvc: 브라우저 요청을 흉내내서 직접 요청 URL을 넘겨주는 테스트 도구
    //          톰캣 서버를 켜지 않고도 요청을 보낼 수 있음
    @Autowired
    protected MockMvc mockMvc;

    // ObjectMapper: Java 객체와 JSON 데이터 사이의 변환을 담당하는 클래스
    //               직렬화와 역직렬화를 모두 담당
    @Autowired
    protected ObjectMapper objectMapper;

    // WebApplicationContext: 웹 전체에서 공유하는 객체. 스프링 웹 설정을 담당
    //                        mockMvcSetUp() 메소드로 MockMvc 객체를 만들 때 사용
    @Autowired
    private WebApplicationContext context;

    // 데이터베이스 접근을 위한 저장소 객체
    @Autowired
    protected BlogRepository blogRepository;

    // 각 테스트가 실행되기 전에 호출되는 메소드
    // mockMvc 객체를 생성하고, 데이터베이스의 모든 데이터 삭제
    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle 메소드: 블로그 글 추가에 성공하는지 테스트")
    @Test
    public void addArticle() throws Exception {
        // given
        // 블로그 주소, 제목, 내용
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        // 블로그 dto 객체 생성
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 json으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        // 데이터베이스의 모든 데이터를 리스트로 가져옴
        List<Article> articles = blogRepository.findAll();

        // 데이터베이스의 블로그 글 개수가 1개인지 확인
        assertThat(articles.size()).isEqualTo(1);
        // 가져온 블로그 글 제목이 title인지 확인
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        // 가져온 블로그 글 내용이 content인지 확인
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }


    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공하는지 테스트")
    @Test
    public void findAllArticles() throws Exception {
        // given
        // 저장할 글 데이터 생성
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        // 저장할 글 객체 생성 후 데이터베이스에 저장
        blogRepository.save(Article.builder()
                                    .title(title)
                                    .content(content)
                                    .build());

        // when
        // get 요청 실행
        // 응답 상태 코드와 글 json 데이터를 반환
        final ResultActions result = mockMvc.perform(get(url)
                                                    .accept(MediaType.APPLICATION_JSON));

        // then
        // status().isOk(): 응답 상태 코드가 200인지 확인
        // json 데이터의 content 값와 title 값이 입력값과 일치하는지 확인
        // $: 응답받은 json 배열 데이터 전체를 의미
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findArticle: 블로그 글 조회에 성공하는지 테스트")
    @Test
    public void findArticle() throws Exception {
        // given
        // 저장할 글 데이터 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        // 저장할 글 객체 생성 후 데이터베이스에 저장
        Article savedArticle = blogRepository.save(Article.builder()
                                            .title(title)
                                            .content(content)
                                            .build());

        // when
        // get 요청 실행
        // 응답 상태 코드와 글 json 데이터 반환
        final ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));

        // then
        // status().isOk(): 응답 상태 코드가 200인지 확인
        // json 데이터의 content 값와 title 값이 입력값과 일치하는지 확인
        // $: 응답받은 json 배열 데이터 전체를 의미
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공하는지 테스트")
    @Test
    public void deleteArticle() throws Exception {
        // given
        // 저장할 글 데이터 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        // 저장할 글 객체 생성 후 데이터베이스에 저장
        Article savedArticle = blogRepository.save(Article.builder()
                                            .title(title)
                                            .content(content)
                                            .build());

        // when
        // 게시글 하나 삭제 요청
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        // then
        // 게시글 전체 조회
        List<Article> articles = blogRepository.findAll();

        // 현재 게시글이 비어있는지 테스트
        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given
        // 저장할 글 데이터 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        // 저장할 글 객체 생성 후 데이터베이스에 저장
        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // 변경할 글 데이터 생성
        final String newTitle = "new title";
        final String newContent = "new content";

        // 변경할 글 데이터를 보관하는 dto 객체 생성
        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // when
        // 게시글 수정 요청
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)));

        // then
        // 응답 상태 코드 테스트
        result.andExpect(status().isOk());

        // 데이터베이스에서 기존 엔티티의 id와 같은 데이터를 갖는 엔티티 가져옴
        Article article = blogRepository.findById(savedArticle.getId()).get();

        // 데이터베이스에서 가져온 엔티티의 데이터가 변경되었는지 확인
        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }
}