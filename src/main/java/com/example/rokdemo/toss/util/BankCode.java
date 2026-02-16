package com.example.rokdemo.toss.util;

import lombok.Getter;

/**
 * 토스페이먼츠 은행 코드 enum
 * 은행 두 자리 코드를 한글명으로 매핑
 */
@Getter
public enum BankCode {
    IBK("03", "기업", "IBK기업은행"),
    KOOKMIN("06", "국민", "KB국민은행"),
    SUHYEOP("07", "수협", "Sh수협은행"),
    NONGHYEOP("11", "농협", "NH농협은행"),
    LOCALNONGHYEOP("12", "단위농협", "단위농협(지역농축협)"),
    WOORI("20", "우리", "우리은행"),
    SC("23", "SC제일", "SC제일은행"),
    CITI("27", "씨티", "씨티은행"),
    SUHYEOPLOCALBANK("30", "수협중앙회", "수협중앙회"),
    DAEGUBANK("31", "대구", "iM뱅크(대구)"),
    BUSANBANK("32", "부산", "부산은행"),
    GWANGJUBANK("34", "광주", "광주은행"),
    JEJUBANK("35", "제주", "제주은행"),
    JEONBUKBANK("37", "전북", "전북은행"),
    KYONGNAMBANK("39", "경남", "경남은행"),
    SAEMAUL("45", "새마을", "새마을금고"),
    SHINHYEOP("48", "신협", "신협"),
    SAVINGBANK("50", "저축", "저축은행중앙회"),
    HSBC("54", "HSBC", "홍콩상하이은행"),
    BOA("60", "BOA", "Bank of America"),
    SANLIM("64", "산림", "산림조합"),
    POST("71", "우체국", "우체국예금보험"),
    HANA("81", "하나", "하나은행"),
    SHINHAN("88", "신한", "신한은행"),
    KBANK("89", "케이", "케이뱅크"),
    KAKAOBANK("90", "카카오", "카카오뱅크"),
    TOSSBANK("92", "토스", "토스뱅크"),
    KDBBANK("02", "산업", "한국산업은행");

    private final String code;
    private final String koreanName;
    private final String fullName;

    BankCode(String code, String koreanName, String fullName) {
        this.code = code;
        this.koreanName = koreanName;
        this.fullName = fullName;
    }

    /**
     * 은행 코드로 BankCode 조회
     * 
     * @param code 은행 두 자리 코드
     * @return BankCode enum 값, 없으면 null
     */
    public static BankCode fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String upperCode = code.trim().toUpperCase();
        for (BankCode bank : BankCode.values()) {
            if (bank.code.equalsIgnoreCase(upperCode)) {
                return bank;
            }
        }

        return null;
    }

    /**
     * 은행 코드를 한글명으로 변환
     * 
     * @param code 은행 두 자리 코드
     * @return 은행 한글명 (예: "농협", "국민"), 없으면 코드 그대로 반환
     */
    public static String toKoreanName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        BankCode bank = fromCode(code);
        return bank != null ? bank.koreanName : code;
    }

    /**
     * 은행 코드를 전체 이름으로 변환
     * 
     * @param code 은행 두 자리 코드
     * @return 은행 전체 이름 (예: "NH농협은행", "KB국민은행"), 없으면 코드 그대로 반환
     */
    public static String toFullName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        BankCode bank = fromCode(code);
        return bank != null ? bank.fullName : code;
    }

    /**
     * 은행 코드가 유효한지 확인
     * 
     * @param code 은행 두 자리 코드
     * @return 유효한 코드인지 여부
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
