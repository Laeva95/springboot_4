package com.shinsunyoung.blog.controller;

import com.shinsunyoung.blog.dto.CreateAccessTokenRequest;
import com.shinsunyoung.blog.dto.CreateAccessTokenResponse;
import com.shinsunyoung.blog.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;     // 토큰 발급 로직을 맡은 서비스

    @PostMapping("/api/token")                   // POST /api/token 요청을 받음
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request) {   // JSON 바디를 요청 객체로 변환

        // 리프레시 토큰으로 새 액세스 토큰 생성
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        System.out.println("새로 생성된 액세스 토큰: " + newAccessToken);

        // 201 Created 상태와 함께 새 액세스 토큰을 응답
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}