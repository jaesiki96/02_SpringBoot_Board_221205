package com.its.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    // 기본 주소 출력
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
