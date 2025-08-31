package org.example.danggeun.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("일반 로그인 시도 - Email: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없음 - Email: {}", username);
                    return new UsernameNotFoundException("이메일이 존재하지 않습니다: " + username);
                });

        // role이 없는 경우 기본값 설정 및 저장
        if (user.getRole() == null || user.getRole().isEmpty()) {
            log.info("사용자 role이 없어서 기본값 설정 - User ID: {}, Email: {}", user.getId(), user.getEmail());
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }

        log.info("일반 로그인 성공 - User ID: {}, Email: {}, Role: {}",
                user.getId(), user.getEmail(), user.getRole());

        return new CustomUserDetails(user);
    }
}