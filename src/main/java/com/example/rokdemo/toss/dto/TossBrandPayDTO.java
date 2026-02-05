package com.example.rokdemo.toss.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 토스페이먼츠 브랜드페이 등록 정보 DTO
 */
@Getter
@Setter
public class TossBrandPayDTO {
    /**
     * PK
     */
    private Integer id;
    
    /**
     * 사용자 IDX
     */
    private Integer userIdx;
    
    /**
     * 토스페이먼츠 customerKey
     */
    private String customerKey;
    
    /**
     * 등록일시
     */
    private LocalDateTime registeredAt;
    
    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}
