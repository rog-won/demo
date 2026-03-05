package com.example.rokdemo.repository;

import com.example.rokdemo.entity.Payment;
import com.example.rokdemo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 결제 Repository
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 주문번호로 조회
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * 결제키로 조회
     */
    Optional<Payment> findByPaymentKey(String paymentKey);

    /**
     * 사용자별 결제 내역 조회
     */
    Page<Payment> findByUser(User user, Pageable pageable);

    /**
     * 사용자별 결제 내역 조회 (상태 필터)
     */
    Page<Payment> findByUserAndStatus(User user, Payment.PaymentStatus status, Pageable pageable);

    /**
     * 기간별 결제 내역 조회
     */
    @Query("SELECT p FROM Payment p WHERE p.approvedAt BETWEEN :startDate AND :endDate")
    List<Payment> findByApprovedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 사용자별 총 결제 금액 조회
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user = :user AND p.status = 'DONE'")
    Long sumAmountByUser(@Param("user") User user);

    /**
     * 특정 상태의 결제 건수 조회
     */
    long countByStatus(Payment.PaymentStatus status);
}
