package com.example.rokdemo.repository;

import com.example.rokdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 중복 체크
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 중복 체크
     */
    boolean existsByEmail(String email);
}
