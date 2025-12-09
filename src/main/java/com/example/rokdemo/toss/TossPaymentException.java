package com.example.rokdemo.toss;

import lombok.Getter;

/**
 * 토스페이먼츠 결제 관련 예외 클래스
 */
@Getter
public class TossPaymentException extends RuntimeException {
    
    /**
     * 토스페이먼츠 에러 코드
     */
    private final String errorCode;
    
    public TossPaymentException(String message) {
        super(message);
        this.errorCode = null;
    }
    
    public TossPaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TossPaymentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    
    public TossPaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 특정 에러 코드인지 확인
     * @param code 확인할 에러 코드
     * @return 일치 여부
     */
    public boolean hasErrorCode(String code) {
        return errorCode != null && errorCode.equals(code);
    }
    
    /**
     * 이미 취소된 결제인지 확인
     * @return 이미 취소된 결제인지 여부
     */
    public boolean isAlreadyCanceled() {
        return hasErrorCode("ALREADY_CANCELED_PAYMENT");
    }
}
