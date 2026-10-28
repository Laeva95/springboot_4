package com.shinsunyoung.blog.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

// 블로그 기능과 관계없는 thymeleaf 연습용 컨트롤러
@Controller
public class ExampleController {

    // Get 요청 처리
    @GetMapping("/thymeleaf/example")
    // Model: view 페이지로 데이터를 전달하기 위한 객체
    public String thymeleafExample(Model model){
        // Person 객체 생성
        Person examplePerson = new Person();

        // Person 객체 값 설정
        examplePerson.setId(1L);
        examplePerson.setName("홍길동");
        examplePerson.setAge(11);
        examplePerson.setHobbies(List.of("운동", "독서"));

        // Model 객체에 Person 객체 저장
        model.addAttribute("person", examplePerson);
        // Model 객체에 현재 날짜 저장
        model.addAttribute("today", LocalDate.now());

        // "templates/example.html" 뷰 페이지로 이동
        return "example";
    }

    @Setter
    @Getter
    class Person{
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}

