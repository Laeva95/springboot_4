package com.shinsunyoung.blog.service;

import com.shinsunyoung.blog.dto.WritingSuggestionRequest;
import com.shinsunyoung.blog.dto.WritingSuggestionsResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

// [8장 · 글쓰기 도우미 · 핵심 서비스]
// 이 클래스가 "AI에게 물어보고 결과를 정리하는" 일을 모두 합니다.
//   1) 프롬프트 템플릿(writing-assistant.st)에 사용자 입력을 채운다  → PromptTemplate
//   2) 완성된 문장을 AI(ChatClient)에게 보낸다                        → ChatClient
//   3) AI의 JSON 답변을 자바 객체로 바꾼다                           → BeanOutputConverter
// 스프링 AI 덕분에 OpenAI, OpenRouter 등 "어떤 AI 회사"를 쓰는지 이 코드는 몰라도 됩니다.
@Service
public class WritingAssistantService {

    // AI와 대화하는 "전화기" 역할입니다. 설정(application.yml)을 보고 스프링이 자동으로 만들어 줍니다.
    private final ChatClient chatClient;

    // 빈칸(중괄호 {...})이 있는 문장 양식을 실제 문장으로 채워 주는 도구입니다.
    private final PromptTemplate template;

    // AI가 준 JSON 글자를 WritingSuggestionsResponse 객체로 바꿔 주는 "번역기"입니다.
    // getFormat()은 "이런 JSON 모양으로 답하라"는 안내문을 만들어 줍니다.
    private final BeanOutputConverter<WritingSuggestionsResponse> outputConverter =
            new BeanOutputConverter<>(WritingSuggestionsResponse.class);

    // 생성자: 스프링이 필요한 재료를 넣어 줍니다(의존성 주입).
    //  - ChatClient.Builder : AI 전화기를 만드는 공장
    //  - promptResource     : src/main/resources/prompts/writing-assistant.st 파일 자체
    public WritingAssistantService(
            ChatClient.Builder chatClientBuilder,
            @Value("classpath:prompts/writing-assistant.st") Resource promptResource
    ) {
        this.chatClient = chatClientBuilder.build();
        this.template = new PromptTemplate(promptResource);
    }

    // 실제 기능: 사용자 입력 → 프롬프트 → AI 호출 → 제안 목록(DTO)
    public WritingSuggestionsResponse getWritingAssist(WritingSuggestionRequest request) {
        // (1) 템플릿의 {title}, {content}, {question}, {format} 자리를 채워서 최종 프롬프트 문장을 만듭니다.
        String prompt = template.create(request.toMap(outputConverter.getFormat())).getContents();

        // (2) 만든 문장을 AI에게 보내고, 답변 글자(content)만 받습니다.
        // prompt: 요청 메세지 생성
        // user: 사용자 메세지에 담음
        // call: AI 서버로 HTTP 요청을 보내고 기다림
        // content: 답변 json에서 답변 글자만 꺼냄
        String response = chatClient.prompt().user(prompt).call().content();
        System.out.println("AI가 답변 제안한 응답 메세지: " + response);
        // (3) AI가 준 JSON 글자를 WritingSuggestionsResponse 객체로 바꿔서 돌려줍니다.
        return outputConverter.convert(response);
    }
}