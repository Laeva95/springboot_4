package com.shinsunyoung.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    // 로그인 페이지 이동
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
