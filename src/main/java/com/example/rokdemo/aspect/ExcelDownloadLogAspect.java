package com.example.rokdemo.aspect;

import com.example.rokdemo.annotation.ExcelDownloadLog;
import com.example.rokdemo.service.AdminLogService;
import com.example.rokdemo.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 엑셀 다운로드 로깅을 위한 AOP Aspect
 * DB 연동 버전
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ExcelDownloadLogAspect {

    private final AdminLogService adminLogService;

    @Around("@annotation(annotation)")
    public Object logExcelDownload(ProceedingJoinPoint joinPoint, ExcelDownloadLog annotation) throws Throwable {
        HttpServletRequest request = getCurrentRequest();

        String username = "unknown";
        String ipAddress = "unknown";
        String userAgent = "unknown";
        String targetResource = "unknown";

        // 기본 정보 설정
        if (request != null) {
            ipAddress = WebUtils.getClientIp(request);
            userAgent = request.getHeader("User-Agent");
            targetResource = request.getRequestURI();
        }

        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            username = authentication.getName();
        }

        String description = annotation.value();
        if (description == null || description.isEmpty()) {
            description = joinPoint.getSignature().getName();
        }
        description = description + " 다운로드";

        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();

            // 성공 로그 기록 (사용자가 인증된 경우에만)
            if (!"unknown".equals(username)) {
                try {
                    // TODO: username으로 User ID 조회 필요
                    // adminLogService.createLog(userId, "EXCEL_DOWNLOAD", description, targetResource, ipAddress, userAgent);
                } catch (Exception e) {
                    // 로그 기록 실패는 무시 (비즈니스 로직에 영향 없도록)
                }
            }

            return result;
        } catch (Exception e) {
            // 실패 로그 기록
            if (!"unknown".equals(username)) {
                try {
                    String errorDescription = description + " 실패: " + e.getMessage();
                    // TODO: username으로 User ID 조회 필요
                    // adminLogService.createLog(userId, "EXCEL_DOWNLOAD_FAILED", errorDescription, targetResource, ipAddress, userAgent);
                } catch (Exception logException) {
                    // 로그 기록 실패는 무시
                }
            }

            throw e;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
