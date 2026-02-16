package com.example.rokdemo.toss.dto;

import lombok.Data;

/**
 * 토스페이먼츠 브랜드페이 Access Token 발급 응답 DTO
 */
@Data
public class TossBrandPayTokenResponse {
    /**
     * 액세스 토큰
     */
    private String accessToken;
    
    /**
     * 토큰 유형 (Bearer)
     */
    private String tokenType;
    
    /**
     * 토큰 유효 시간 (초 단위)
     */
    private Integer expiresIn;
    
    /**
     * 에러 코드 (실패 시)
     */
    private String code;
    
    /**
     * 에러 메시지 (실패 시)
     */
    private String message;
}
