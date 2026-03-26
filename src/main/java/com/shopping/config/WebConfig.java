package com.shopping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 대해
                .allowedOrigins("http://localhost:3000", "http://34.60.156.156", "http://34.60.156.156:8080") // 허용할 프론트엔드 주소들
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 작업들
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키나 인증 정보 허용 여부
                .maxAge(3600);
    }
}
