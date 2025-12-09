package com.example.rokdemo.toss.dto;

import lombok.Data;

/**
 * 토스페이먼츠 결제 취소 요청 DTO
 */
@Data
public class TossCancelRequest {
    /**
     * 취소 사유
     */
    private String cancelReason;
    
    /**
     * 취소 금액 (부분 취소인 경우)
     */
    private Integer cancelAmount;
    
    /**
     * 환불 계좌 정보 (가상계좌 결제 취소 시 필요)
     */
    private RefundReceiveAccount refundReceiveAccount;
    
    /**
     * 환불 계좌 정보 DTO
     */
    @Data
    public static class RefundReceiveAccount {
        /**
         * 은행 코드
         */
        private String bank;
        
        /**
         * 계좌번호
         */
        private String accountNumber;
        
        /**
         * 예금주명
         */
        private String holderName;
    }
}

