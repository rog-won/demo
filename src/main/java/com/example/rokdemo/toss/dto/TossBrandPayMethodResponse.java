package com.example.rokdemo.toss.dto;

import lombok.Data;
import java.util.List;

/**
 * 토스페이먼츠 브랜드페이 결제수단 조회 응답 DTO
 */
@Data
public class TossBrandPayMethodResponse {
    /**
     * 고객 식별자
     */
    private String customerKey;
    
    /**
     * 등록된 결제수단 목록
     */
    private List<BrandPayMethod> methods;
    
    /**
     * 에러 코드 (실패 시)
     */
    private String code;
    
    /**
     * 에러 메시지 (실패 시)
     */
    private String message;
    
    /**
     * 브랜드페이 결제수단 정보
     */
    @Data
    public static class BrandPayMethod {
        /**
         * 결제수단 ID
         */
        private String methodKey;
        
        /**
         * 결제수단 타입 (CARD 등)
         */
        private String methodType;
        
        /**
         * 카드 정보
         */
        private CardInfo card;
    }
    
    /**
     * 카드 정보
     */
    @Data
    public static class CardInfo {
        /**
         * 카드 발급사 코드
         */
        private String issuerCode;
        
        /**
         * 카드 매입사 코드
         */
        private String acquirerCode;
        
        /**
         * 카드 번호 (마스킹)
         */
        private String number;
        
        /**
         * 카드 타입 (체크/신용)
         */
        private String cardType;
        
        /**
         * 카드 소유자 타입 (개인/법인)
         */
        private String ownerType;
    }
}
