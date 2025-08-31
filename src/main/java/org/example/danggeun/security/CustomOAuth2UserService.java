package org.example.danggeun.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.info("OAuth2 로그인 시도 - Provider: {}, Email: {}, Name: {}", provider, email, name);

        // 이메일로 기존 사용자 찾기
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
                    log.info("기존 사용자 찾음 - ID: {}, Email: {}", existingUser.getId(), existingUser.getEmail());
                    existingUser.setUsername(name);
                    existingUser.setProvider(provider);
                    existingUser.setProviderId(providerId);

                    // role이 없으면 설정
                    if (existingUser.getRole() == null || existingUser.getRole().isEmpty()) {
                        existingUser.setRole("ROLE_USER");
                    }

                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 새 사용자 생성
                    log.info("새 사용자 생성 - Email: {}, Name: {}", email, name);
                    User newUser = User.builder()
                            .email(email)
                            .username(name)
                            .provider(provider)
                            .providerId(providerId)
                            .password(UUID.randomUUID().toString()) // OAuth2 사용자는 랜덤 패스워드
                            .role("ROLE_USER") // 역할 설정
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });

        log.info("OAuth2 사용자 처리 완료 - User ID: {}, Email: {}, Role: {}",
                user.getId(), user.getEmail(), user.getRole());

        // OAuth2User 반환 (이메일을 name attribute로 사용)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                oAuth2User.getAttributes(),
                "email" // name attribute를 email로 변경 (sub 대신)
        );
    }
}