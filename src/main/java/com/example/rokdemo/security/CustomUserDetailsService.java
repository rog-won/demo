package com.example.rokdemo.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    // 임시 사용자 저장소 (실제로는 DB를 사용해야 합니다)
    private final Map<String, String> users = new HashMap<>();

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        // 테스트용 사용자 추가 (username: admin, password: admin123)
        users.put("admin", passwordEncoder.encode("admin123"));
        users.put("user", passwordEncoder.encode("user123"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = users.get(username);
        if (password == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        return User.builder()
                .username(username)
                .password(password)
                .authorities(new ArrayList<>())
                .build();
    }
}
