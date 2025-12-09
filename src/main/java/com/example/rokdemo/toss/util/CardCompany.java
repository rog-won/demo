package com.example.rokdemo.toss.util;

import lombok.Getter;

/**
 * 토스페이먼츠 카드사 enum
 * 카드 발급사 두 자리 코드를 한글명으로 매핑
 */
@Getter
public enum CardCompany {
    KOOKMIN("11", "국민", "KB국민카드"),
    HANA("21", "하나", "하나카드"),
    BC("31", "BC", "BC카드"),
    WOORI_BC("33", "우리", "우리BC카드(BC 매입)"),
    SUHYEOP("34", "수협", "Sh수협은행"),
    JEONBUK("35", "전북", "전북은행"),
    CITI("36", "씨티", "씨티카드"),
    POST("37", "우체국", "우체국예금보험"),
    SAEMAUL("38", "새마을", "새마을금고"),
    SAVINGBANK("39", "저축", "저축은행중앙회"),
    KBANK("3A", "케이뱅크", "케이뱅크"),
    IBK_BC("3K", "기업비씨", "기업 BC"),
    SHINHAN("41", "신한", "신한카드"),
    JEJUBANK("42", "제주", "제주은행"),
    GWANGJUBANK("46", "광주", "광주은행"),
    SAMSUNG("51", "삼성", "삼성카드"),
    HYUNDAI("61", "현대", "현대카드"),
    SHINHYEOP("62", "신협", "신협"),
    LOTTE("71", "롯데", "롯데카드"),
    NONGHYEOP("91", "농협", "NH농협카드"),
    WOORI("W1", "우리", "우리카드(우리 매입)"),
    KAKAOBANK("15", "카카오뱅크", "카카오뱅크"),
    TOSSBANK("24", "토스뱅크", "토스뱅크"),
    KDBBANK("30", "산업", "한국산업은행");

    private final String code;
    private final String koreanName;
    private final String fullName;

    CardCompany(String code, String koreanName, String fullName) {
        this.code = code;
        this.koreanName = koreanName;
        this.fullName = fullName;
    }

    /**
     * 카드사 코드로 CardCompany 조회
     * 
     * @param code 카드 발급사 두 자리 코드
     * @return CardCompany enum 값, 없으면 null
     */
    public static CardCompany fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String upperCode = code.trim().toUpperCase();
        for (CardCompany company : CardCompany.values()) {
            if (company.code.equalsIgnoreCase(upperCode)) {
                return company;
            }
        }

        return null;
    }

    /**
     * 카드사 코드를 한글명으로 변환
     * 
     * @param code 카드 발급사 두 자리 코드
     * @return 카드사 한글명 (예: "현대", "국민"), 없으면 코드 그대로 반환
     */
    public static String toKoreanName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        CardCompany company = fromCode(code);
        return company != null ? company.koreanName : code;
    }

    /**
     * 카드사 코드를 전체 이름으로 변환
     * 
     * @param code 카드 발급사 두 자리 코드
     * @return 카드사 전체 이름 (예: "현대카드", "KB국민카드"), 없으면 코드 그대로 반환
     */
    public static String toFullName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        CardCompany company = fromCode(code);
        return company != null ? company.fullName : code;
    }

    /**
     * 카드사 코드가 유효한지 확인
     * 
     * @param code 카드 발급사 두 자리 코드
     * @return 유효한 코드인지 여부
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}

