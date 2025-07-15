package org.example.danggeun.security;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // OAuth2 기본 유저 정보 가져오기 (구글 API 호출 포함)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 플랫폼(구글/네이버 등)인지
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google

        // 플랫폼이 제공하는 유저 고유 ID
        String providerId = oAuth2User.getAttribute("sub"); // 구글은 "sub"

        // 공통 정보
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // DB에 해당 이메일 유저가 없으면 새로 저장, 있으면 기존 유저 사용
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    return userRepository.save(newUser);
                });

        // OAuth2 로그인 세션 정보 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "sub" // 사용자 고유 식별 키 (Spring Security 기준)
        );
    }
}