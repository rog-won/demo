package com.example.rokdemo.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String getGreetingMessage() {
        return "스프링 부트 + 타임리프 베이스 프로젝트 입니다.";
    }
}