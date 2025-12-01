package com.example.rokdemo.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("appName", "My Web App");
        model.addAttribute("footerText", "© 2025 My Company");
    }
}