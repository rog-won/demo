package com.example.rokdemo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 사용자 상태 변경
     */
    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * 사용자 역할
     */
    public enum UserRole {
        USER, ADMIN
    }

    /**
     * 사용자 상태
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
