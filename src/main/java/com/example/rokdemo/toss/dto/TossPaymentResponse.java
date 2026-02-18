package com.example.rokdemo.toss.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 토스페이먼츠 결제 응답 DTO
 */
@Data
public class TossPaymentResponse {
    /**
     * 결제 키
     */
    private String paymentKey;
    
    /**
     * 주문번호
     */
    private String orderId;
    
    /**
     * 주문명
     */
    private String orderName;
    
    /**
     * 결제 타입 (NORMAL: 일반결제, BILLING: 자동결제, BRANDPAY: 브랜드페이)
     */
    private String type;
    
    /**
     * 결제 상태 (DONE, CANCELED, PARTIAL_CANCELED, WAITING_FOR_DEPOSIT 등)
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
     * 공급가액
     */
    private Integer suppliedAmount;
    
    /**
     * 부가세
     */
    private Integer vat;
    
    /**
     * 결제 요청 시각
     */
    @SerializedName("requestedAt")
    private String requestedAt;
    
    /**
     * 결제 승인 시각
     */
    @SerializedName("approvedAt")
    private String approvedAt;
    
    /**
     * 결제 수단 (카드, 가상계좌, 계좌이체 등)
     */
    private String method;
    
    /**
     * 카드 정보 (카드 결제인 경우)
     */
    private TossCardInfo card;
    
    /**
     * 가상계좌 정보 (가상계좌 결제인 경우)
     */
    private TossVirtualAccountInfo virtualAccount;
    
    /**
     * 간편결제 정보 (간편결제인 경우)
     */
    private TossEasyPayInfo easyPay;
    
    /**
     * 취소 이력
     */
    private List<TossCancelHistory> cancels;
    
    /**
     * 에러 코드
     */
    private String code;
    
    /**
     * 에러 메시지
     */
    private String message;
    
    /**
     * 카드 정보 DTO
     */
    @Data
    public static class TossCardInfo {
        @SerializedName("amount")
        private Integer amount;  // 카드사에 결제 요청한 금액
        
        @SerializedName("issuerCode")
        private String issuerCode;  // 카드 발급사 두 자리 코드 (예: "11", "21", "41")
        
        @SerializedName("acquirerCode")
        private String acquirerCode;  // 카드 매입사 두 자리 코드
        
        @SerializedName("number")
        private String number;   // 카드번호 (마스킹)
        
        @SerializedName("installmentPlanMonths")
        private Integer installmentPlanMonths;  // 할부 개월 (일시불이면 0)
        
        @SerializedName("approveNo")
        private String approveNo;  // 카드사 승인 번호
        
        @SerializedName("useCardPoint")
        private Boolean useCardPoint;  // 카드사 포인트 사용 여부
        
        @SerializedName("cardType")
        private String cardType;  // 카드 종류 (신용, 체크, 기프트, 미확인)
        
        @SerializedName("ownerType")
        private String ownerType;  // 카드 소유자 타입 (개인, 법인, 미확인)
        
        @SerializedName("acquireStatus")
        private String acquireStatus;  // 매입 상태
        
        @SerializedName("isInterestFree")
        private Boolean isInterestFree;  // 무이자 할부 적용 여부
        
        @SerializedName("interestPayer")
        private String interestPayer;  // 할부 수수료 부담 주체 (BUYER, CARD_COMPANY, MERCHANT)
        
    }
    
    /**
     * 가상계좌 정보 DTO
     */
    @Data
    public static class TossVirtualAccountInfo {
        private String accountType;  // 계좌 타입
        private String accountNumber;  // 계좌번호
        private String bankCode;  // 은행 코드
        private String customerName;  // 입금자명
        private LocalDateTime dueDate;  // 입금 기한
        private String refundStatus;  // 환불 상태
        private LocalDateTime expiredAt;  // 만료 시각
        private LocalDateTime settlementStatus;  // 정산 상태
    }
    
    /**
     * 간편결제 정보 DTO
     */
    @Data
    public static class TossEasyPayInfo {
        @SerializedName("provider")
        private String provider;  // 간편결제 제공사 (KAKAOPAY, NAVERPAY, TOSSPAY, SAMSUNGPAY, LPAY 등)
        
        @SerializedName("amount")
        private Integer amount;  // 간편결제 금액
        
        @SerializedName("discountAmount")
        private Integer discountAmount;  // 할인 금액
    }
    
    /**
     * 취소 이력 DTO
     */
    @Data
    public static class TossCancelHistory {
        private String cancelReason;  // 취소 사유
        private Integer cancelAmount;  // 취소 금액
        private String taxFreeAmount;  // 면세 금액
        private String taxAmount;  // 과세 금액
        private String refundableAmount;  // 환불 가능 금액
        private String easyPayDiscountAmount;  // 간편결제 할인 금액
        private String canceledAt;  // 취소 시각
        private String transactionKey;  // 거래 키
        private String receiptKey;  // 영수증 키
    }
}

