package com.example.rokdemo.aspect;

import com.example.rokdemo.annotation.ExcelDownloadLog;
import com.example.rokdemo.common.Const;
import com.example.rokdemo.dto.AdminLogVO;
import com.example.rokdemo.dto.AdminVO;
import com.example.rokdemo.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 엑셀 다운로드 로깅을 위한 AOP Aspect
 */
@Aspect
@Component
public class ExcelDownloadLogAspect {

    @Around("@annotation(annotation)")
    public Object logExcelDownload(ProceedingJoinPoint joinPoint, ExcelDownloadLog annotation) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        AdminLogVO logVO = new AdminLogVO();
        
        // 기본 정보 설정
        if (request != null) {
            String loginId = (String) request.getSession().getAttribute(Const.LOGIN_ID);
            logVO.setAdminId(loginId != null ? loginId : "unknown");
            logVO.setUrl(request.getRequestURI());
            logVO.setIpAddress(WebUtils.getClientIp(request));
            
            // name 가져오기
            if (request.getUserPrincipal() != null) {
                AdminVO adminVO = (AdminVO) ((Authentication) request.getUserPrincipal()).getPrincipal();
                logVO.setName(adminVO.getName());
            }
        } else {
            logVO.setAdminId("unknown");
            logVO.setUrl("unknown");
            logVO.setIpAddress("unknown");
        }
        
        String description = annotation.value();
        if (description == null || description.isEmpty()) {
            description = joinPoint.getSignature().getName();
        }
        logVO.setContent(description + " 다운로드");
        
        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();
            
            // 성공 로그 기록
            logVO.setSuccess(true);
//            accountService.insertExcelDownloadLog(logVO);
            
            return result;
        } catch (Exception e) {
            // 실패 로그 기록
            logVO.setSuccess(false);
            logVO.setErrorMessage(e.getMessage());
//            accountService.insertExcelDownloadLog(logVO);
            
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
