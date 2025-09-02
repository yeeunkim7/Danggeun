package org.example.danggeun.security;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Slf4j
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        log.debug("CustomUserDetails 생성 - User ID: {}, Email: {}", user.getId(), user.getEmail());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole();
        if (role == null || role.isEmpty()) {
            role = "ROLE_USER"; // 기본 역할 설정
        }
        log.debug("사용자 권한 반환 - Role: {}", role);
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true = 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격증명 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true = 활성화됨)
    }

    // 추가 편의 메소드들
    public Long getUserId() {
        return user.getId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getRealUsername() {
        return user.getUsername();
    }
}