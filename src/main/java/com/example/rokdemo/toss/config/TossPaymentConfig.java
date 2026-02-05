package com.example.rokdemo.toss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 토스페이먼츠 설정 관리 클래스
 */
@Configuration
@ConfigurationProperties(prefix = "toss.payment")
@Getter
@Setter
public class TossPaymentConfig {
    /**
     * 시크릿 키 (서버 사이드 인증용)
     */
    private String secretKey;

    /**
     * 클라이언트 키 (클라이언트 사이드 인증용)
     */
    private String clientKey;

    /**
     * 결제 위젯 variantKey
     */
    private String variantKey;

    /**
     * 결제 승인 API URL
     */
    private String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";

    /**
     * 결제 취소 API URL
     */
    private String cancelUrl = "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel";

    /**
     * 결제 조회 API URL
     */
    private String lookupUrl = "https://api.tosspayments.com/v1/payments/{paymentKey}";

    /**
     * 빌링(자동결제) 승인 API URL
     */
    private String billingUrl = "https://api.tosspayments.com/v1/billing/{billingKey}";

    /**
     * 브랜드페이 Access Token 발급 API URL
     */
    private String brandPayTokenUrl = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";

    /**
     * 브랜드페이 결제수단 조회 API URL
     */
    private String brandPayMethodUrl = "https://api.tosspayments.com/v1/brandpay/cards";

    /**
     * 브랜드페이 리다이렉트 URL (개발자센터 등록용)
     */
    private String brandPayRedirectUrl;
}