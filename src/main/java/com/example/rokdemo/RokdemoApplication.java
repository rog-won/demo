package com.example.rokdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RokdemoApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(RokdemoApplication.class, args);
    }

    // WAR로 톰캣에서 기동될 때 진입점
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RokdemoApplication.class);
    }
}
