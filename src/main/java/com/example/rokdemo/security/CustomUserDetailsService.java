package com.example.rokdemo.security;

import com.example.rokdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Spring Security UserDetailsService 구현
 * DB에서 사용자 정보를 조회하여 인증에 사용
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.rokdemo.entity.User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 사용자 상태 체크
        if (user.getStatus() != com.example.rokdemo.entity.User.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("비활성화된 사용자입니다: " + username);
        }

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            ))
            .build();
    }
}
