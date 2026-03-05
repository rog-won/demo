package com.example.rokdemo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 관리자 로그 엔티티
 */
@Entity
@Table(name = "admin_logs", indexes = {
    @Index(name = "idx_admin_id", columnList = "admin_id"),
    @Index(name = "idx_action_type", columnList = "actionType"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, length = 50)
    private String actionType; // EXCEL_DOWNLOAD, LOGIN, LOGOUT, etc.

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String targetResource;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;
}
