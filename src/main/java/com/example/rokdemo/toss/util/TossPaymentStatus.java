package com.example.rokdemo.toss.util;

import lombok.Getter;

/**
 * 토스페이먼츠 결제 상태 enum
 */
@Getter
public enum TossPaymentStatus {
    // 결제 진행 중
    READY("READY", "결제 대기 중"),
    IN_PROGRESS("IN_PROGRESS", "결제 진행 중"),
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT", "입금 대기 중"),
    
    // 결제 완료
    DONE("DONE", "결제 완료"),
    
    // 결제 취소
    CANCELED("CANCELED", "전체 취소"),
    PARTIAL_CANCELED("PARTIAL_CANCELED", "부분 취소"),
    
    // 결제 실패
    ABORTED("ABORTED", "결제 승인 실패"),
    EXPIRED("EXPIRED", "결제 만료");

    private final String code;
    private final String description;

    TossPaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 상태 코드로 TossPaymentStatus 조회
     * @param code 상태 코드
     * @return TossPaymentStatus enum 값, 없으면 null
     */
    public static TossPaymentStatus fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        for (TossPaymentStatus status : TossPaymentStatus.values()) {
            if (status.code.equalsIgnoreCase(code.trim())) {
                return status;
            }
        }

        return null;
    }

    /**
     * 결제 완료 상태인지 확인
     * @param code 상태 코드
     * @return 결제 완료 여부
     */
    public static boolean isDone(String code) {
        return DONE.code.equals(code);
    }

    /**
     * 취소 상태인지 확인 (전체/부분)
     * @param code 상태 코드
     * @return 취소 상태 여부
     */
    public static boolean isCanceled(String code) {
        return CANCELED.code.equals(code) || PARTIAL_CANCELED.code.equals(code);
    }

    /**
     * 실패 상태인지 확인
     * @param code 상태 코드
     * @return 실패 상태 여부
     */
    public static boolean isFailed(String code) {
        return ABORTED.code.equals(code) || EXPIRED.code.equals(code);
    }

    /**
     * 진행 중 상태인지 확인
     * @param code 상태 코드
     * @return 진행 중 여부
     */
    public static boolean isInProgress(String code) {
        return READY.code.equals(code) || IN_PROGRESS.code.equals(code) || WAITING_FOR_DEPOSIT.code.equals(code);
    }
}

