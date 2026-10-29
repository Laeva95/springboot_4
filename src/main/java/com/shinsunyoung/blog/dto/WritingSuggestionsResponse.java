package com.shinsunyoung.blog.dto;

import java.util.List;

// [8장 · 글쓰기 도우미 · 응답 데이터]
// AI가 준 답을 "제안 목록"으로 담아 브라우저에 돌려주는 "응답 상자"입니다.
// AI는 글자(JSON 문자열)로 답하는데, BeanOutputConverter가 이 record 형태로 바꿔 줍니다.
// 응답 예) {"suggestions": ["제안 1", "제안 2", "제안 3"]}
public record WritingSuggestionsResponse(List<String> suggestions) {}