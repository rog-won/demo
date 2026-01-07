package com.example.rokdemo.toss.util;

import org.springframework.ui.Model;

/**
 * 토스페이먼츠 URL 생성 유틸리티
 */
public class TossPaymentUrlHelper {
    
    /**
     * 토스페이먼츠 결제 URL을 Model에 추가
     * 
     * @param model
     * @param homepageUrl
     * @param basePath Controller 기본 경로 (예: /tourProgram)
     * @param clientKey 토스페이먼츠 클라이언트 키
     */
    public static void setPaymentUrls(Model model, String homepageUrl, 
                                      String basePath, String clientKey) {
        if (model == null || homepageUrl == null || basePath == null || clientKey == null) {
            throw new IllegalArgumentException("필수 파라미터가 null입니다.");
        }
        
        // basePath 정규화 (앞에 / 없으면 추가, 끝에 / 있으면 제거)
        String normalizedBasePath = basePath;
        if (!normalizedBasePath.startsWith("/")) {
            normalizedBasePath = "/" + normalizedBasePath;
        }
        if (normalizedBasePath.endsWith("/")) {
            normalizedBasePath = normalizedBasePath.substring(0, normalizedBasePath.length() - 1);
        }
        
        model.addAttribute("tossClientKey", clientKey);
        model.addAttribute("tossSuccessUrl", homepageUrl + normalizedBasePath + "/result?success=true");
        model.addAttribute("tossFailUrl", homepageUrl + normalizedBasePath + "/result?success=false");
    }
    
    /**
     * 토스페이먼츠 성공 URL 생성
     * 
     * @param homepageUrl 홈페이지 기본 URL
     * @param basePath Controller 기본 경로
     * @return 성공 URL
     */
    public static String buildSuccessUrl(String homepageUrl, String basePath) {
        return homepageUrl + normalizePath(basePath) + "/result?success=true";
    }
    
    /**
     * 토스페이먼츠 실패 URL 생성
     * 
     * @param homepageUrl 홈페이지 기본 URL
     * @param basePath Controller 기본 경로
     * @return 실패 URL
     */
    public static String buildFailUrl(String homepageUrl, String basePath) {
        return homepageUrl + normalizePath(basePath) + "/result?success=false";
    }
    
    /**
     * 경로 정규화 (앞에 / 없으면 추가, 끝에 / 있으면 제거)
     */
    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        
        String normalized = path;
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}

