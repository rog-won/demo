package com.example.rokdemo.toss.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.List;

/**
 * 토스페이먼츠 결제 취소 응답 DTO
 */
@Data
public class TossCancelResponse {
    /**
     * 결제 키
     */
    private String paymentKey;
    
    /**
     * 주문번호
     */
    private String orderId;
    
    /**
     * 결제 상태 (CANCELED, PARTIAL_CANCELED)
     */
    private String status;
    
    /**
     * 총 결제 금액
     */
    private Integer totalAmount;
    
    /**
     * 취소 가능 금액
     */
    private Integer balanceAmount;
    
    /**
     * 취소 금액
     */
    private Integer cancelAmount;
    
    /**
     * 취소 시각
     */
    @SerializedName("canceledAt")
    private String canceledAt;
    
    /**
     * 취소 사유
     */
    private String cancelReason;
    
    /**
     * 취소 이력
     */
    private List<TossCancelHistory> cancels;
    
    /**
     * 취소 이력 DTO
     */
    @Data
    public static class TossCancelHistory {
        private String cancelReason;
        private Integer cancelAmount;
        private String taxFreeAmount;
        private String taxAmount;
        private String refundableAmount;
        private String easyPayDiscountAmount;
        private String canceledAt;
        private String transactionKey;
        private String receiptKey;
    }
}

