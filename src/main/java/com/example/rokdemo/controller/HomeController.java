package com.example.rokdemo.controller;

import com.example.rokdemo.service.GreetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final GreetingService greetingService;

    // 생성자 주입 (DI)
    public HomeController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "홈");
        model.addAttribute("message", greetingService.getGreetingMessage());
        return "pages/home"; // templates/pages/home.html
    }
}