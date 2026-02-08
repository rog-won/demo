package com.example.rokdemo.toss;

import com.example.rokdemo.toss.dto.TossBrandPayTokenResponse;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 토스페이먼츠 브랜드페이 공통 컨트롤러
 */
@Controller
@RequestMapping("/payment/toss")
public class TossBrandPayController {
    
    private static final Logger logger = LoggerFactory.getLogger(TossBrandPayController.class);
    
    @Value("${nc.api.url}")
    private String apiUrl;
    
    /**
     * 브랜드페이 콜백 처리
     * 
     * @param userIdx
     * @param code 토스페이먼츠 인증 코드
     * @param customerKey 고객 키
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @param request
     * @param model
     * @return JSON 응답 또는 브랜드페이 콜백페이지
     */
//    @Auth(isOptional = true)
    @RequestMapping("/brandpay/callback")
    public Object brandpayCallback(@RequestAttribute(required = false) Integer userIdx,
                                   @RequestParam(required = false) String code,
                                   @RequestParam(required = false) String customerKey,
                                   @RequestParam(required = false) String errorCode,
                                   @RequestParam(required = false) String errorMessage,
                                   HttpServletRequest request,
                                   Model model) {
        
        logger.info("=== 브랜드페이 콜백 요청 ===");
        logger.info("userIdx: {}, customerKey: {}, code: {}, errorCode: {}", 
            userIdx, customerKey, code != null ? "EXISTS" : "NULL", errorCode);
        
        // Accept 헤더로 JSON/HTML 요청 구분
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonRequest = acceptHeader != null && 
            (acceptHeader.contains("application/json") || acceptHeader.contains("*/*"));
        
        logger.info("Accept: {}, isJsonRequest: {}", acceptHeader, isJsonRequest);
        
        try {
            // 1. 에러 체크
            if (errorCode != null && !errorCode.isEmpty()) {
                logger.warn("브랜드페이 등록 실패 - errorCode: {}, errorMessage: {}", errorCode, errorMessage);
                
                if (isJsonRequest) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("code", errorCode);
                    errorResponse.put("message", errorMessage);
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(errorResponse);
                }
                
                model.addAttribute("message", "브랜드페이 등록 실패: " + errorMessage + " (" + errorCode + ")");
                model.addAttribute("closeWindow", true);
                return "payment/brandpayCallback";
            }
            
            // 2. 필수 파라미터 검증
            if (code == null || code.isEmpty()) {
                logger.error("브랜드페이 콜백 파라미터 누락 - code가 없습니다.");
                
                if (isJsonRequest) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("code", "MISSING_PARAMETER");
                    errorResponse.put("message", "code 파라미터가 필요합니다.");
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(errorResponse);
                }
                
                model.addAttribute("message", "브랜드페이 등록 중 오류가 발생했습니다. (파라미터 누락: code)");
                model.addAttribute("closeWindow", true);
                return "payment/brandpayCallback";
            }
            
            if (customerKey == null || customerKey.isEmpty()) {
                logger.error("브랜드페이 콜백 파라미터 누락 - customerKey가 없습니다.");
                
                if (isJsonRequest) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("code", "MISSING_PARAMETER");
                    errorResponse.put("message", "customerKey 파라미터가 필요합니다.");
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(errorResponse);
                }
                
                model.addAttribute("message", "브랜드페이 등록 중 오류가 발생했습니다. (파라미터 누락: customerKey)");
                model.addAttribute("closeWindow", true);
                return "payment/brandpayCallback";
            }
            
            // 3. API 호출 (Access Token 발급)
            String centralApiUrl = apiUrl + "/auth/payment/toss/customerToken?code=" + code + "&customerKey=" + customerKey;
            
            logger.info("=== API 호출 시작 ===");
            logger.info("API URL: {}", centralApiUrl);
            logger.info("customerKey: {}", customerKey);
            
            TossBrandPayTokenResponse tokenResponse = callCentralApi(centralApiUrl);
            
            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                logger.error("API에서 유효하지 않은 응답 수신 - tokenResponse: {}", tokenResponse);
                
                if (isJsonRequest) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("code", "API_ERROR");
                    errorResponse.put("message", "Access Token 발급 실패");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(errorResponse);
                }
                
                model.addAttribute("message", "브랜드페이 등록 중 오류가 발생했습니다. (API 응답 오류)");
                model.addAttribute("closeWindow", true);
                return "payment/brandpayCallback";
            }
            
            logger.info("브랜드페이 Access Token 발급 완료 - customerKey: {}, accessToken: {}", 
                customerKey, maskToken(tokenResponse.getAccessToken()));
            
            // 4. 성공 응답
            if (isJsonRequest) {
                // 토스 SDK가 기대하는 JSON 응답 형식
                logger.info("JSON 응답 반환 (토스 SDK 요청)");
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(tokenResponse);
            }
            
            // 브라우저 리다이렉트용 HTML 응답
            logger.info("HTML 응답 반환 (브라우저 리다이렉트)");
            model.addAttribute("message", "브랜드페이 등록이 완료되었습니다.");
            model.addAttribute("closeWindow", true);
            
            logger.info("=== 브랜드페이 콜백 처리 완료 ===");
            return "payment/brandpayCallback";
            
        } catch (Exception e) {
            logger.error("브랜드페이 콜백 처리 중 예상치 못한 오류 발생", e);
            
            if (isJsonRequest) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("code", "INTERNAL_ERROR");
                errorResponse.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }
            
            model.addAttribute("message", "브랜드페이 등록 중 오류가 발생했습니다.");
            model.addAttribute("closeWindow", true);
            return "payment/brandpayCallback";
        }
    }
    
    /**
     * Access Token 발급
     *
     * @param apiUrl
     * @return Access Token 응답 객체
     * @throws Exception API 호출 실패 시
     */
    private TossBrandPayTokenResponse callCentralApi(String apiUrl) throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);
            
            logger.debug("HTTP GET 요청 전송 중...");
            response = httpClient.execute(httpGet);
            
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            logger.info("=== API 응답 ===");
            logger.info("Status Code: {}", statusCode);
            logger.debug("Response Body: {}", responseBody);
            
            // HTTP 상태 코드 검증
            if (statusCode != 200) {
                logger.error("API 오류 응답 - Status: {}, Body: {}", statusCode, responseBody);
                throw new TossPaymentException("CENTRAL_API_ERROR", 
                    "중앙 API 호출 실패 (Status: " + statusCode + ")");
            }
            
            // JSON 파싱
            Gson gson = new Gson();
            TossBrandPayTokenResponse tokenResponse = gson.fromJson(responseBody, TossBrandPayTokenResponse.class);
            
            if (tokenResponse == null) {
                logger.error("JSON 파싱 실패 - responseBody: {}", responseBody);
                throw new TossPaymentException("JSON_PARSE_ERROR", "응답 JSON 파싱 실패");
            }
            
            logger.debug("JSON 파싱 완료 - accessToken: {}", 
                tokenResponse.getAccessToken() != null ? "EXISTS" : "NULL");
            
            return tokenResponse;
            
        } finally {
            // 리소스 정리
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    logger.warn("CloseableHttpResponse 닫기 실패", e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    logger.warn("CloseableHttpClient 닫기 실패", e);
                }
            }
        }
    }
    
    /**
     * 토큰 마스킹 (로그용)
     * 
     * @param token 원본 토큰
     * @return 마스킹된 토큰 (예: "live_bpac_****...****")
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "****";
        }
        return token.substring(0, 10) + "****...****" + token.substring(token.length() - 4);
    }
}
