package com.example.rokdemo.service;

import com.example.rokdemo.entity.AdminLog;
import com.example.rokdemo.entity.User;
import com.example.rokdemo.repository.AdminLogRepository;
import com.example.rokdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자 로그 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;
    private final UserRepository userRepository;

    /**
     * 로그 생성
     */
    @Transactional
    public AdminLog createLog(Long adminId, String actionType, String description,
                               String targetResource, String ipAddress, String userAgent) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        AdminLog log = AdminLog.builder()
            .admin(admin)
            .actionType(actionType)
            .description(description)
            .targetResource(targetResource)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        return adminLogRepository.save(log);
    }

    /**
     * 관리자별 로그 조회
     */
    public Page<AdminLog> findByAdmin(Long adminId, Pageable pageable) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        return adminLogRepository.findByAdmin(admin, pageable);
    }

    /**
     * 액션 타입별 로그 조회
     */
    public Page<AdminLog> findByActionType(String actionType, Pageable pageable) {
        return adminLogRepository.findByActionType(actionType, pageable);
    }
}
