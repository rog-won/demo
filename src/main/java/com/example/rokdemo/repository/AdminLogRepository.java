package com.example.rokdemo.repository;

import com.example.rokdemo.entity.AdminLog;
import com.example.rokdemo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 로그 Repository
 */
@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    /**
     * 관리자별 로그 조회
     */
    Page<AdminLog> findByAdmin(User admin, Pageable pageable);

    /**
     * 액션 타입별 로그 조회
     */
    Page<AdminLog> findByActionType(String actionType, Pageable pageable);

    /**
     * 관리자 및 액션 타입별 로그 조회
     */
    Page<AdminLog> findByAdminAndActionType(User admin, String actionType, Pageable pageable);

    /**
     * 기간별 로그 조회
     */
    List<AdminLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * IP 주소별 로그 조회
     */
    List<AdminLog> findByIpAddress(String ipAddress);
}
