package com.example.rokdemo.service;

import com.example.rokdemo.entity.User;
import com.example.rokdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성
     */
    @Transactional
    public User createUser(String username, String password, String email, String name) {
        // 중복 체크
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }

        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(email)
            .name(name)
            .role(User.UserRole.USER)
            .status(User.UserStatus.ACTIVE)
            .build();

        return userRepository.save(user);
    }

    /**
     * 사용자명으로 조회
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 이메일로 조회
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 사용자 ID로 조회
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(newPassword));
    }

    /**
     * 사용자 상태 변경
     */
    @Transactional
    public void changeUserStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.changeStatus(status);
    }
}
