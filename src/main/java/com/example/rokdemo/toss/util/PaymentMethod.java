package com.example.rokdemo.toss.util;

import lombok.Getter;

/**
 * 토스페이먼츠 결제 수단 enum
 * 토스페이먼츠 API가 반환하는 한글명을 영어 코드로 매핑
 */
@Getter
public enum PaymentMethod {
    CARD("카드", "카드 결제"),
    EASY_PAY("간편결제", "간편결제 (네이버페이, 카카오페이, 토스페이 등)"),
    VIRTUAL_ACCOUNT("가상계좌", "가상계좌"),
    TRANSFER("계좌이체", "계좌이체"),
    MOBILE_PHONE("휴대폰", "휴대폰 소액결제"),
    CULTURE_GIFT_CERTIFICATE("문화상품권", "문화상품권"),
    BOOK_GIFT_CERTIFICATE("도서문화상품권", "도서문화상품권"),
    GAME_GIFT_CERTIFICATE("게임문화상품권", "게임문화상품권");

    private final String koreanName;
    private final String description;

    PaymentMethod(String koreanName, String description) {
        this.koreanName = koreanName;
        this.description = description;
    }

    /**
     * 한글명 또는 영어 코드로 PaymentMethod 조회
     * 
     * @param value 결제 수단 (한글명 또는 영어 코드)
     * @return PaymentMethod enum 값, 없으면 null
     */
    public static PaymentMethod fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmed = value.trim();

        // 영어 코드로 조회 (enum name)
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(trimmed)) {
                return method;
            }
        }

        // 한글명으로 조회
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.koreanName.equals(trimmed)) {
                return method;
            }
        }

        return null;
    }

    /**
     * 한글명 또는 영어 코드를 영어 코드로 변환
     * 
     * @param value 결제 수단 (한글명 또는 영어 코드)
     * @return 영어 코드 (CARD, EASY_PAY 등), 알 수 없으면 입력값을 대문자로 반환
     */
    public static String toEnglishCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "UNKNOWN";
        }

        PaymentMethod method = fromValue(value);
        if (method != null) {
            return method.name();
        }

        // 알 수 없는 값은 대문자로 변환하여 반환
        return value.trim().toUpperCase();
    }

    /**
     * 영어 코드를 한글명으로 변환
     * 
     * @param code 영어 코드 (CARD, EASY_PAY 등)
     * @return 한글명, 알 수 없으면 입력값 그대로 반환
     */
    public static String toKoreanName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        PaymentMethod method = fromValue(code);
        return method != null ? method.koreanName : code;
    }

    /**
     * 결제 수단 코드가 유효한지 확인
     * 
     * @param value 결제 수단 (한글명 또는 영어 코드)
     * @return 유효한 코드인지 여부
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }
}

