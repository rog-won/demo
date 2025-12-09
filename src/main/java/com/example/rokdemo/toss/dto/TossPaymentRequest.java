package com.example.rokdemo.toss.dto;

import lombok.Data;

/**
 * 토스페이먼츠 결제 승인 요청 DTO
 */
@Data
public class TossPaymentRequest {
    /**
     * 클라이언트에서 받은 paymentKey
     */
    private String paymentKey;
    
    /**
     * 주문번호
     */
    private String orderId;
    
    /**
     * 결제 금액
     */
    private Integer amount;
}

