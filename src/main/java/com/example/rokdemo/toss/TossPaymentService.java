package com.example.rokdemo.toss;

import com.example.rokdemo.toss.config.TossPaymentConfig;
import com.example.rokdemo.toss.dto.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 토스페이먼츠 결제 공통 서비스
 */
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(TossPaymentService.class);
    private static final Gson gson = new Gson();
    
    // HTTP 요청 타임아웃 설정
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 30000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000;
    
    private final TossPaymentConfig config;
    
    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connectionManager;
    
    /**
     * HttpClient 초기화
     */
    @PostConstruct
    public void init() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);  // 최대 커넥션 수 : 트레픽 많으면 100~200 사이로 고려하면 됨
        connectionManager.setDefaultMaxPerRoute(20);  // 호스트당 최대 커넥션 수 : 이것도 트레픽 많으면 20~50 사이로 고려하면 됨

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .build();
        
        httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        logger.info("토스페이먼츠 HttpClient 초기화 완료");
    }
    
    /**
     * HttpClient 종료
     */
    @PreDestroy
    public void destroy() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            if (connectionManager != null) {
                connectionManager.close();
            }
            logger.info("토스페이먼츠 HttpClient 종료 완료");
        } catch (Exception e) {
            logger.warn("토스페이먼츠 HttpClient 종료 중 오류: {}", e.getMessage());
        }
    }
    
    /**
     * Basic Auth 헤더 생성
     */
    private String createAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
            (config.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );
    }
    
    /**
     * 공통 헤더 설정
     */
    private void setCommonHeaders(HttpRequestBase request) {
        request.setHeader("Authorization", createAuthHeader());
        request.setHeader("Content-Type", "application/json");
    }
    
    /**
     * HTTP 응답 처리 공통 로직
     */
    private <T> T executeRequest(HttpRequestBase request, Class<T> responseType, String operationType) throws TossPaymentException {
        setCommonHeaders(request);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("토스페이먼츠 {} 응답 - HTTP {}: {}", operationType, statusCode, responseBody);

            if (statusCode < 200 || statusCode >= 300) {
                handleErrorResponse(responseBody, statusCode, operationType);
            }

            if (responseType == Void.class || responseBody.isEmpty()) {
                return null;
            }

            return gson.fromJson(responseBody, responseType);

        } catch (TossPaymentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("토스페이먼츠 {} 처리 중 오류: {}", operationType, e.getMessage(), e);
            throw new TossPaymentException(operationType + " 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 에러 응답 처리
     */
    private void handleErrorResponse(String responseBody, int statusCode, String operationType) throws TossPaymentException {
        try {
            JsonObject errorJson = gson.fromJson(responseBody, JsonObject.class);
            String errorCode = errorJson.has("code") ? errorJson.get("code").getAsString() : null;
            String errorMessage = errorJson.has("message") ? errorJson.get("message").getAsString() : null;
            
            String finalMessage = errorMessage != null ? errorMessage : operationType + " 실패 (HTTP " + statusCode + ")";
            
            logger.warn("토스페이먼츠 {} 실패 - 코드: {}, 메시지: {}", operationType, errorCode, finalMessage);
            
            throw new TossPaymentException(errorCode, finalMessage);
            
        } catch (TossPaymentException e) {
            throw e;
        } catch (Exception parseException) {
            throw new TossPaymentException(operationType + " 실패 (HTTP " + statusCode + "): " + responseBody);
        }
    }
    
    /**
     * 결제 승인 처리
     * @param req 결제 승인 요청 정보
     * @return 결제 승인 결과
     * @throws TossPaymentException 결제 승인 실패 시
     */
    public TossPaymentResponse confirmPayment(TossPaymentRequest req) throws TossPaymentException {
        logger.info("토스페이먼츠 결제 승인 요청 - orderId: {}, amount: {}", req.getOrderId(), req.getAmount());
        
        HttpPost httpPost = new HttpPost(config.getConfirmUrl());
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("paymentKey", req.getPaymentKey());
        requestBody.addProperty("orderId", req.getOrderId());
        requestBody.addProperty("amount", req.getAmount());
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("요청 데이터 생성 실패", e);
        }
        
        TossPaymentResponse paymentResponse = executeRequest(httpPost, TossPaymentResponse.class, "결제 승인");
        
        // 응답 null 체크
        if (paymentResponse == null) {
            throw new TossPaymentException("결제 승인 응답이 비어있습니다.");
        }

        // 결제 상태 확인 (에러 코드가 있으면 실패)
        if (paymentResponse.getCode() != null) {
            throw new TossPaymentException(
                paymentResponse.getCode(),
                paymentResponse.getMessage() != null ? paymentResponse.getMessage() : "결제 승인 실패: " + paymentResponse.getCode()
            );
        }
        
        logger.info("토스페이먼츠 결제 승인 성공 - paymentKey: {}, status: {}", 
            paymentResponse.getPaymentKey(), paymentResponse.getStatus());
        
        return paymentResponse;
    }
    
    /**
     * 결제 취소 처리
     * @param paymentKey 결제 키
     * @param req 취소 요청 정보
     * @return 취소 결과
     * @throws TossPaymentException 결제 취소 실패 시
     */
    public TossCancelResponse cancelPayment(String paymentKey, TossCancelRequest req) throws TossPaymentException {
        logger.info("토스페이먼츠 결제 취소 요청 - paymentKey: {}, cancelReason: {}, cancelAmount: {}", 
            paymentKey, req.getCancelReason(), req.getCancelAmount());
        
        String cancelUrl = config.getCancelUrl().replace("{paymentKey}", paymentKey);
        HttpPost httpPost = new HttpPost(cancelUrl);
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("cancelReason", req.getCancelReason());
        
        // 취소 금액이 지정된 경우 (부분 취소)
        if (req.getCancelAmount() != null) {
            requestBody.addProperty("cancelAmount", req.getCancelAmount());
        }
        
        // 환불 계좌 정보가 있는 경우 (가상계좌 결제 취소 시)
        if (req.getRefundReceiveAccount() != null) {
            JsonObject account = new JsonObject();
            account.addProperty("bank", req.getRefundReceiveAccount().getBank());
            account.addProperty("accountNumber", req.getRefundReceiveAccount().getAccountNumber());
            account.addProperty("holderName", req.getRefundReceiveAccount().getHolderName());
            requestBody.add("refundReceiveAccount", account);
        }
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("요청 데이터 생성 실패", e);
        }
        
        TossCancelResponse cancelResponse = executeRequest(httpPost, TossCancelResponse.class, "결제 취소");
        
        // 응답 null 체크
        if (cancelResponse == null) {
            throw new TossPaymentException("결제 취소 응답이 비어있습니다.");
        }
        
        logger.info("토스페이먼츠 결제 취소 성공 - paymentKey: {}, status: {}", paymentKey, cancelResponse.getStatus());
        
        return cancelResponse;
    }
    
    /**
     * 결제 조회
     * @param paymentKey 결제 키
     * @return 결제 정보
     * @throws TossPaymentException 결제 조회 실패 시
     */
    public TossPaymentResponse lookupPayment(String paymentKey) throws TossPaymentException {
        logger.info("토스페이먼츠 결제 조회 요청 - paymentKey: {}", paymentKey);
        
        String lookupUrl = config.getLookupUrl().replace("{paymentKey}", paymentKey);
        HttpGet httpGet = new HttpGet(lookupUrl);
        
        TossPaymentResponse paymentResponse = executeRequest(httpGet, TossPaymentResponse.class, "결제 조회");
        
        // 응답 null 체크
        if (paymentResponse == null) {
            throw new TossPaymentException("결제 조회 응답이 비어있습니다.");
        }
        
        // 결제 상태 확인 (에러 코드가 있으면 실패)
        if (paymentResponse.getCode() != null) {
            throw new TossPaymentException(
                paymentResponse.getCode(),
                paymentResponse.getMessage() != null ? paymentResponse.getMessage() : "결제 조회 실패: " + paymentResponse.getCode()
            );
        }
        
        logger.info("토스페이먼츠 결제 조회 성공 - paymentKey: {}, status: {}", paymentKey, paymentResponse.getStatus());
        
        return paymentResponse;
    }
    
    /**
     * 빌링 승인 처리
     * NC 다이노스 페이에서 등록된 빌링키로 결제를 진행.
     * 
     * @param req 빌링 결제 요청 정보
     * @return 결제 승인 결과
     * @throws TossPaymentException 빌링 결제 실패 시
     */
    public TossPaymentResponse billingPayment(TossBillingRequest req) throws TossPaymentException {
        logger.info("토스페이먼츠 빌링 결제 요청 - billingKey: {}, customerKey: {}, orderId: {}, amount: {}",
                maskBillingKey(req.getBillingKey()),
                req.getCustomerKey(),
                req.getOrderId(),
                req.getAmount());
        // 필수 파라미터 검증
        if (req.getBillingKey() == null || req.getBillingKey().isEmpty()) {
            throw new TossPaymentException("INVALID_BILLING_KEY", "빌링키가 없습니다.");
        }
        if (req.getCustomerKey() == null || req.getCustomerKey().isEmpty()) {
            throw new TossPaymentException("INVALID_CUSTOMER_KEY", "고객 식별자가 없습니다.");
        }
        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new TossPaymentException("INVALID_AMOUNT", "결제 금액이 올바르지 않습니다.");
        }
        if (req.getOrderId() == null || req.getOrderId().isEmpty()) {
            throw new TossPaymentException("INVALID_ORDER_ID", "주문번호가 없습니다.");
        }
        if (req.getOrderName() == null || req.getOrderName().isEmpty()) {
            throw new TossPaymentException("INVALID_ORDER_NAME", "상품명이 없습니다.");
        }
        
        // 빌링 API URL 생성
        String billingUrl = config.getBillingUrl().replace("{billingKey}", req.getBillingKey());
        HttpPost httpPost = new HttpPost(billingUrl);
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("customerKey", req.getCustomerKey());
        requestBody.addProperty("amount", req.getAmount());
        requestBody.addProperty("orderId", req.getOrderId());
        requestBody.addProperty("orderName", req.getOrderName());
        
        // 선택 파라미터 추가
        if (req.getCustomerEmail() != null && !req.getCustomerEmail().isEmpty()) {
            requestBody.addProperty("customerEmail", req.getCustomerEmail());
        }
        if (req.getCustomerName() != null && !req.getCustomerName().isEmpty()) {
            requestBody.addProperty("customerName", req.getCustomerName());
        }
        if (req.getCustomerMobilePhone() != null && !req.getCustomerMobilePhone().isEmpty()) {
            requestBody.addProperty("customerMobilePhone", req.getCustomerMobilePhone());
        }
        if (req.getTaxFreeAmount() != null) {
            requestBody.addProperty("taxFreeAmount", req.getTaxFreeAmount());
        }
        if (req.getCardInstallmentPlan() != null && req.getCardInstallmentPlan() > 0) {
            requestBody.addProperty("cardInstallmentPlan", req.getCardInstallmentPlan());
        }
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("REQUEST_BUILD_ERROR", "요청 데이터 생성 실패", e);
        }
        
        logger.debug("토스페이먼츠 빌링 요청 Body: {}", requestBody);
        
        TossPaymentResponse paymentResponse = executeRequest(httpPost, TossPaymentResponse.class, "빌링 결제");
        
        // 응답 null 체크
        if (paymentResponse == null) {
            throw new TossPaymentException("EMPTY_RESPONSE", "빌링 결제 응답이 비어있습니다.");
        }
        
        // 결제 상태 확인 (에러 코드가 있으면 실패)
        if (paymentResponse.getCode() != null) {
            throw new TossPaymentException(
                paymentResponse.getCode(),
                paymentResponse.getMessage() != null ? paymentResponse.getMessage() : "빌링 결제 실패: " + paymentResponse.getCode()
            );
        }
        
        // 결제 완료 상태가 아니면 실패 처리
        if (!"DONE".equals(paymentResponse.getStatus())) {
            throw new TossPaymentException(
                "PAYMENT_NOT_DONE",
                "빌링 결제가 완료되지 않았습니다. 상태: " + paymentResponse.getStatus()
            );
        }
        
        logger.info("토스페이먼츠 빌링 결제 성공 - paymentKey: {}, orderId: {}, status: {}", 
            paymentResponse.getPaymentKey(), paymentResponse.getOrderId(), paymentResponse.getStatus());
        
        return paymentResponse;
    }
    
    /**
     * 빌링키 마스킹 (로그용)
     */
    private String maskBillingKey(String billingKey) {
        if (billingKey == null || billingKey.length() < 10) {
            return "***";
        }
        return billingKey.substring(0, 5) + "***" + billingKey.substring(billingKey.length() - 5);
    }
    
    /**
     * 클라이언트 키 조회 (JSP에서 사용)
     * @return 클라이언트 키
     */
    public String getClientKey() {
        return config.getClientKey();
    }
    
    /**
     * 결제 위젯 variantKey
     * @return variantKey
     */
    public String getVariantKey() {
        return config.getVariantKey();
    }
    
    /**
     * 브랜드페이 리다이렉트 URL 조회 (JSP에서 사용)
     * @return 브랜드페이 리다이렉트 URL
     */
    public String getBrandPayRedirectUrl() {
        return config.getBrandPayRedirectUrl();
    }
    
    /**
     * 브랜드페이 Access Token 발급
     * @param code 토스페이먼츠에서 발급한 인증 코드
     * @param customerKey 고객 식별자
     * @return Access Token 정보
     * @throws TossPaymentException Access Token 발급 실패 시
     */
    public TossBrandPayTokenResponse issueBrandPayAccessToken(String code, String customerKey) throws TossPaymentException {
        logger.info("토스페이먼츠 브랜드페이 Access Token 발급 요청 - customerKey: {}", customerKey);
        
        // 필수 파라미터 검증
        if (code == null || code.isEmpty()) {
            throw new TossPaymentException("INVALID_CODE", "인증 코드가 없습니다.");
        }
        if (customerKey == null || customerKey.isEmpty()) {
            throw new TossPaymentException("INVALID_CUSTOMER_KEY", "고객 식별자가 없습니다.");
        }
        
        HttpPost httpPost = new HttpPost(config.getBrandPayTokenUrl());
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("grantType", "AuthorizationCode");
        requestBody.addProperty("code", code);
        requestBody.addProperty("customerKey", customerKey);
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("REQUEST_BUILD_ERROR", "요청 데이터 생성 실패", e);
        }
        
        logger.debug("토스페이먼츠 브랜드페이 Access Token 요청 Body: {}", requestBody);
        
        TossBrandPayTokenResponse tokenResponse = executeRequest(httpPost, TossBrandPayTokenResponse.class, "브랜드페이 Access Token 발급");
        
        // 응답 null 체크
        if (tokenResponse == null) {
            throw new TossPaymentException("EMPTY_RESPONSE", "브랜드페이 Access Token 응답이 비어있습니다.");
        }
        
        // 에러 응답 체크
        if (tokenResponse.getCode() != null) {
            throw new TossPaymentException(
                tokenResponse.getCode(),
                tokenResponse.getMessage() != null ? tokenResponse.getMessage() : "브랜드페이 Access Token 발급 실패: " + tokenResponse.getCode()
            );
        }
        
        // Access Token 존재 확인
        if (tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isEmpty()) {
            throw new TossPaymentException("NO_ACCESS_TOKEN", "Access Token이 없습니다.");
        }
        
        logger.info("토스페이먼츠 브랜드페이 Access Token 발급 성공 - customerKey: {}", customerKey);
        
        return tokenResponse;
    }
    
    /**
     * 브랜드페이 등록된 결제수단 조회
     * @param customerKey 고객 식별자
     * @return 등록된 결제수단 목록
     * @throws TossPaymentException 조회 실패 시
     */
    public TossBrandPayMethodResponse getBrandPayMethods(String customerKey) throws TossPaymentException {
        logger.info("토스페이먼츠 브랜드페이 결제수단 조회 요청 - customerKey: {}", customerKey);
        
        // 필수 파라미터 검증
        if (customerKey == null || customerKey.isEmpty()) {
            throw new TossPaymentException("INVALID_CUSTOMER_KEY", "고객 식별자가 없습니다.");
        }
        
        String url = config.getBrandPayMethodUrl() + "?customerKey=" + customerKey;
        HttpGet httpGet = new HttpGet(url);
        
        TossBrandPayMethodResponse methodResponse = executeRequest(httpGet, TossBrandPayMethodResponse.class, "브랜드페이 결제수단 조회");
        
        // 응답 null 체크
        if (methodResponse == null) {
            throw new TossPaymentException("EMPTY_RESPONSE", "브랜드페이 결제수단 조회 응답이 비어있습니다.");
        }
        
        // 에러 응답 체크
        if (methodResponse.getCode() != null) {
            throw new TossPaymentException(
                methodResponse.getCode(),
                methodResponse.getMessage() != null ? methodResponse.getMessage() : "브랜드페이 결제수단 조회 실패: " + methodResponse.getCode()
            );
        }
        
        logger.info("토스페이먼츠 브랜드페이 결제수단 조회 성공 - customerKey: {}, 등록된 결제수단: {}개", 
            customerKey, methodResponse.getMethods() != null ? methodResponse.getMethods().size() : 0);
        
        return methodResponse;
    }
}