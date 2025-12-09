package com.example.rokdemo.toss;

import com.ddfactory.nc_homepage.payment.toss.config.TossPaymentConfig;
import com.ddfactory.nc_homepage.payment.toss.dto.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
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
 * 여러 모듈에서 재사용 가능한 결제 로직 제공
 */
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(TossPaymentService.class);
    private static final Gson gson = new Gson();
    
    // HTTP 요청 타임아웃 설정 (밀리초)
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 30000;
    
    private final TossPaymentConfig config;
    
    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connectionManager;
    
    /**
     * HttpClient 초기화 (재사용을 위한 커넥션 풀 설정)
     */
    @PostConstruct
    public void init() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);  // 최대 커넥션 수
        connectionManager.setDefaultMaxPerRoute(20);  // 호스트당 최대 커넥션 수
        
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
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
        try {
            setCommonHeaders(request);
            
            CloseableHttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();
            
            logger.debug("토스페이먼츠 {} 응답 - HTTP {}: {}", operationType, statusCode, responseBody);
            
            if (statusCode != 200) {
                handleErrorResponse(responseBody, statusCode, operationType);
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
            
            String finalMessage = errorMessage != null 
                ? errorMessage 
                : operationType + " 실패 (HTTP " + statusCode + ")";
            
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
     * @param request 결제 승인 요청 정보
     * @return 결제 승인 결과
     * @throws TossPaymentException 결제 승인 실패 시
     */
    public TossPaymentResponse confirmPayment(TossPaymentRequest request) throws TossPaymentException {
        logger.info("토스페이먼츠 결제 승인 요청 - orderId: {}, amount: {}", request.getOrderId(), request.getAmount());
        
        HttpPost httpPost = new HttpPost(config.getConfirmUrl());
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("paymentKey", request.getPaymentKey());
        requestBody.addProperty("orderId", request.getOrderId());
        requestBody.addProperty("amount", request.getAmount());
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("요청 데이터 생성 실패", e);
        }
        
        TossPaymentResponse paymentResponse = executeRequest(httpPost, TossPaymentResponse.class, "결제 승인");
        
        // 결제 상태 확인
        if (paymentResponse.getCode() != null) {
            throw new TossPaymentException(
                paymentResponse.getCode(),
                paymentResponse.getMessage() != null 
                    ? paymentResponse.getMessage() 
                    : "결제 승인 실패: " + paymentResponse.getCode()
            );
        }
        
        logger.info("토스페이먼츠 결제 승인 성공 - paymentKey: {}, status: {}", 
            paymentResponse.getPaymentKey(), paymentResponse.getStatus());
        
        return paymentResponse;
    }
    
    /**
     * 결제 취소 처리
     * @param paymentKey 결제 키
     * @param request 취소 요청 정보
     * @return 취소 결과
     * @throws TossPaymentException 결제 취소 실패 시
     */
    public TossCancelResponse cancelPayment(String paymentKey, TossCancelRequest request) throws TossPaymentException {
        logger.info("토스페이먼츠 결제 취소 요청 - paymentKey: {}, cancelReason: {}, cancelAmount: {}", 
            paymentKey, request.getCancelReason(), request.getCancelAmount());
        
        String cancelUrl = config.getCancelUrl().replace("{paymentKey}", paymentKey);
        HttpPost httpPost = new HttpPost(cancelUrl);
        
        // 요청 Body 생성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("cancelReason", request.getCancelReason());
        
        // 취소 금액이 지정된 경우 (부분 취소)
        if (request.getCancelAmount() != null) {
            requestBody.addProperty("cancelAmount", request.getCancelAmount());
        }
        
        // 환불 계좌 정보가 있는 경우 (가상계좌 결제 취소 시)
        if (request.getRefundReceiveAccount() != null) {
            JsonObject account = new JsonObject();
            account.addProperty("bank", request.getRefundReceiveAccount().getBank());
            account.addProperty("accountNumber", request.getRefundReceiveAccount().getAccountNumber());
            account.addProperty("holderName", request.getRefundReceiveAccount().getHolderName());
            requestBody.add("refundReceiveAccount", account);
        }
        
        try {
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TossPaymentException("요청 데이터 생성 실패", e);
        }
        
        TossCancelResponse cancelResponse = executeRequest(httpPost, TossCancelResponse.class, "결제 취소");
        
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
        
        // 결제 상태 확인
        if (paymentResponse.getCode() != null) {
            throw new TossPaymentException(
                paymentResponse.getCode(),
                paymentResponse.getMessage() != null 
                    ? paymentResponse.getMessage() 
                    : "결제 조회 실패: " + paymentResponse.getCode()
            );
        }
        
        logger.info("토스페이먼츠 결제 조회 성공 - paymentKey: {}, status: {}", paymentKey, paymentResponse.getStatus());
        
        return paymentResponse;
    }
    
    /**
     * 클라이언트 키 조회 (JSP에서 사용)
     * @return 클라이언트 키
     */
    public String getClientKey() {
        return config.getClientKey();
    }
    
    /**
     * 성공 URL 조회
     * @return 성공 URL
     */
    public String getSuccessUrl() {
        return config.getSuccessUrl();
    }
    
    /**
     * 실패 URL 조회
     * @return 실패 URL
     */
    public String getFailUrl() {
        return config.getFailUrl();
    }
}
