package com.shinsunyoung.blog.dto;

import java.util.Map;

// [8장 · 글쓰기 도우미 · 요청 데이터]
// 글 작성 화면의 "AI 도움받기" 창에서 입력한 값을 서버로 보낼 때 쓰는 "택배 상자"입니다.
// 브라우저가 보낸 JSON {"title": "...", "content": "...", "question": "..."} 이
// 이 record 객체로 자동 변환됩니다(@RequestBody 덕분).
//
// record: Java 14부터 정식 도입된 "불변(한 번 만들면 못 바꾸는) 데이터 객체"입니다.
// 필드, 생성자, getter가 자동으로 만들어져서 DTO를 짧게 쓸 수 있습니다.
public record WritingSuggestionRequest(
        String title,     // 지금 작성 중인 글 제목
        String content,   // 지금 작성 중인 글 본문
        String question   // "어떤 부분이 고민되시나요?"에 적은 질문
) {

    // 프롬프트 템플릿(resources/prompts/writing-assistant.st)의 빈칸을 채울 값들을 Map으로 묶습니다.
    // Map.of의 "키"("title", "content" ...)가 템플릿의 {title}, {content} ... 이름과 똑같아야 합니다.
    // format은 BeanOutputConverter가 만든 "JSON으로 답하라"는 안내 문장입니다.
    public Map<String, Object> toMap(String format) {
        return Map.of(
                "title", title,
                "content", content,
                "question", question,
                "format", format
        );
    }
}