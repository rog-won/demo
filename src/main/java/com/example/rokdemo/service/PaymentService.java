package com.example.rokdemo.service;

import com.example.rokdemo.entity.Payment;
import com.example.rokdemo.entity.User;
import com.example.rokdemo.repository.PaymentRepository;
import com.example.rokdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 결제 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    /**
     * 결제 생성
     */
    @Transactional
    public Payment createPayment(Long userId, String orderId, String orderName, Long amount,
                                  String customerEmail, String customerName, String customerMobilePhone) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Payment payment = Payment.builder()
            .user(user)
            .orderId(orderId)
            .orderName(orderName)
            .amount(amount)
            .customerEmail(customerEmail)
            .customerName(customerName)
            .customerMobilePhone(customerMobilePhone)
            .status(Payment.PaymentStatus.READY)
            .build();

        return paymentRepository.save(payment);
    }

    /**
     * 결제 승인
     */
    @Transactional
    public Payment approvePayment(String orderId, String paymentKey, String method) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + orderId));

        payment.approve(paymentKey, method, LocalDateTime.now());
        return payment;
    }

    /**
     * 결제 취소
     */
    @Transactional
    public Payment cancelPayment(String paymentKey, String cancelReason, Long canceledAmount) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        if (canceledAmount != null && canceledAmount < payment.getAmount()) {
            // 부분 취소
            payment.partialCancel(canceledAmount, cancelReason, LocalDateTime.now());
        } else {
            // 전체 취소
            payment.cancel(cancelReason, canceledAmount, LocalDateTime.now());
        }

        return payment;
    }

    /**
     * 결제 실패 처리
     */
    @Transactional
    public Payment failPayment(String orderId, String errorCode, String errorMessage) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + orderId));

        payment.fail(errorCode, errorMessage);
        return payment;
    }

    /**
     * 주문번호로 조회
     */
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    /**
     * 결제키로 조회
     */
    public Optional<Payment> findByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey);
    }

    /**
     * 사용자별 결제 내역 조회
     */
    public Page<Payment> findByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return paymentRepository.findByUser(user, pageable);
    }

    /**
     * 사용자별 총 결제 금액 조회
     */
    public Long getTotalAmountByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return paymentRepository.sumAmountByUser(user);
    }
}
