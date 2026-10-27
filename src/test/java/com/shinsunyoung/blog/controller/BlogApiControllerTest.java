package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

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

}