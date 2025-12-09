package com.example.rokdemo.toss.util;

import lombok.Getter;

/**
 * 토스페이먼츠 간편결제 제공사 enum
 * 토스페이먼츠 API가 반환하는 한글명을 영어 코드로 매핑
 */
@Getter
public enum EasyPayProvider {
    KAKAOPAY("KAKAOPAY", "카카오페이"),
    NAVERPAY("NAVERPAY", "네이버페이"),
    TOSSPAY("TOSSPAY", "토스페이"),
    SAMSUNGPAY("SAMSUNGPAY", "삼성페이"),
    LPAY("LPAY", "LG페이"),
    PAYCO("PAYCO", "페이코"),
    SSGPAY("SSGPAY", "SSG페이"),
    APPLEPAY("APPLEPAY", "애플페이");

    private final String code;
    private final String koreanName;

    EasyPayProvider(String code, String koreanName) {
        this.code = code;
        this.koreanName = koreanName;
    }

    /**
     * 한글명 또는 영어 코드로 EasyPayProvider 조회
     * 
     * @param value 간편결제 제공사 (한글명 또는 영어 코드)
     * @return EasyPayProvider enum 값, 없으면 null
     */
    public static EasyPayProvider fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmed = value.trim();

        // 영어 코드로 조회 (enum code)
        for (EasyPayProvider provider : EasyPayProvider.values()) {
            if (provider.code.equalsIgnoreCase(trimmed)) {
                return provider;
            }
        }

        // 한글명으로 조회
        for (EasyPayProvider provider : EasyPayProvider.values()) {
            if (provider.koreanName.equals(trimmed)) {
                return provider;
            }
        }

        return null;
    }

    /**
     * 한글명 또는 영어 코드를 영어 코드로 변환
     * 
     * @param value 간편결제 제공사 (한글명 또는 영어 코드)
     * @return 영어 코드 (KAKAOPAY, NAVERPAY 등), 알 수 없으면 입력값을 대문자로 반환
     */
    public static String toEnglishCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        EasyPayProvider provider = fromValue(value);
        if (provider != null) {
            return provider.code;
        }

        // 알 수 없는 값은 대문자로 변환하여 반환
        return value.trim().toUpperCase();
    }

    /**
     * 영어 코드를 한글명으로 변환
     * 
     * @param code 영어 코드 (KAKAOPAY, NAVERPAY 등)
     * @return 한글명, 알 수 없으면 입력값 그대로 반환
     */
    public static String toKoreanName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        EasyPayProvider provider = fromValue(code);
        return provider != null ? provider.koreanName : code;
    }

    /**
     * 제공사 코드가 유효한지 확인
     * 
     * @param value 간편결제 제공사 (한글명 또는 영어 코드)
     * @return 유효한 코드인지 여부
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }
}

