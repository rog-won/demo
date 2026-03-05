package com.example.rokdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 결제 엔티티
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_order_id", columnList = "orderId"),
    @Index(name = "idx_payment_key", columnList = "paymentKey"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(length = 200)
    private String orderName;

    @Column(length = 200)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 50)
    private String method; // 카드, 가상계좌, 간편결제 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.READY;

    @Column(length = 100)
    private String customerEmail;

    @Column(length = 100)
    private String customerName;

    @Column(length = 20)
    private String customerMobilePhone;

    private LocalDateTime approvedAt;

    private LocalDateTime canceledAt;

    @Column(length = 500)
    private String cancelReason;

    private Long canceledAmount;

    @Column(length = 50)
    private String errorCode;

    @Column(length = 500)
    private String errorMessage;

    /**
     * 결제 승인
     */
    public void approve(String paymentKey, String method, LocalDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.status = PaymentStatus.DONE;
        this.approvedAt = approvedAt;
    }

    /**
     * 결제 취소
     */
    public void cancel(String cancelReason, Long canceledAmount, LocalDateTime canceledAt) {
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = cancelReason;
        this.canceledAmount = canceledAmount != null ? canceledAmount : this.amount;
        this.canceledAt = canceledAt;
    }

    /**
     * 부분 취소
     */
    public void partialCancel(Long canceledAmount, String cancelReason, LocalDateTime canceledAt) {
        this.status = PaymentStatus.PARTIAL_CANCELED;
        this.canceledAmount = (this.canceledAmount != null ? this.canceledAmount : 0L) + canceledAmount;
        this.cancelReason = cancelReason;
        this.canceledAt = canceledAt;
    }

    /**
     * 결제 실패
     */
    public void fail(String errorCode, String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 결제 상태
     */
    public enum PaymentStatus {
        READY,              // 결제 대기
        IN_PROGRESS,        // 결제 진행 중
        WAITING_FOR_DEPOSIT, // 가상계좌 입금 대기
        DONE,               // 결제 완료
        CANCELED,           // 결제 취소
        PARTIAL_CANCELED,   // 부분 취소
        ABORTED,            // 결제 중단
        EXPIRED,            // 만료
        FAILED              // 실패
    }
}
