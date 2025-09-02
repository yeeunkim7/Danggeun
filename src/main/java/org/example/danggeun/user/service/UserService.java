package org.example.danggeun.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(String email, String rawPassword, String username, String phone) {
        log.info("회원가입 시도 - Email: {}, Username: {}", email, username);

        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            log.warn("이메일 중복: {}", email);
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 사용자명 중복 검사
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("사용자명 중복: {}", username);
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다.");
        }

        try {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .username(username)
                    .phone(phone)
                    .role("ROLE_USER")
                    .nickname(username) // 초기 닉네임을 사용자명으로 설정
                    .build();

            User savedUser = userRepository.save(user);
            log.info("사용자 저장 성공 - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

            // 저장 후 즉시 조회하여 실제로 저장되었는지 확인
            User verifyUser = userRepository.findById(savedUser.getId())
                    .orElseThrow(() -> new RuntimeException("저장된 사용자를 다시 조회할 수 없습니다."));

            log.info("저장 검증 완료 - ID: {}", verifyUser.getId());
            return savedUser.getId();

        } catch (DataIntegrityViolationException e) {
            log.error("데이터 무결성 제약 조건 위반: {}", e.getMessage());
            if (e.getMessage().contains("user_email")) {
                throw new IllegalArgumentException("이미 가입된 이메일입니다.");
            } else if (e.getMessage().contains("user_nm")) {
                throw new IllegalArgumentException("이미 사용중인 사용자명입니다.");
            }
            throw new RuntimeException("회원가입 처리 중 데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("회원가입 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}