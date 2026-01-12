package com.example.rokdemo.toss.dto;

import lombok.Data;

/**
 * 토스페이먼츠 빌링(자동결제) 승인 요청 DTO
 */
@Data
public class TossBillingRequest {
    /**
     * 빌링키 (NCPay 등록 빌링키)
     */
    private String billingKey;
    
    /**
     * 식별자 (useridx)
     */
    private String customerKey;
    
    /**
     * 결제 금액
     */
    private Integer amount;
    
    /**
     * 주문번호 (고유값)
     */
    private String orderId;
    
    /**
     * 상품명
     */
    private String orderName;
    
    /**
     * 이메일
     */
    private String customerEmail;
    
    /**
     * 이름
     */
    private String customerName;
    
    /**
     * 전화번호
     */
    private String customerMobilePhone;
    
    /**
     * 과세 제외 금액 (기본값: 0)
     */
    private Integer taxFreeAmount;
    
    /**
     * 할부 개월 수 (선택, 기본값: 일시불)
     * 0: 일시불, 2~12: 할부
     */
    private Integer cardInstallmentPlan;
}

